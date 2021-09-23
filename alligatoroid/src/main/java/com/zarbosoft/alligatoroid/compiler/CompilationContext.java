package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.cache.Cache;
import com.zarbosoft.alligatoroid.compiler.jvm.MultiError;
import com.zarbosoft.alligatoroid.compiler.jvmshared.DynamicClassLoader;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedClass;
import com.zarbosoft.alligatoroid.compiler.language.Block;
import com.zarbosoft.alligatoroid.compiler.languagedeserialize.LanguageDeserializer;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class CompilationContext {
  public static final String METHOD_NAME = "enter";
  public static final String METHOD_DESCRIPTOR = JVMDescriptor.func(JVMDescriptor.voidDescriptor());
  public static long uniqueClass = 0;
  /*public final ExecutorService executor =
  new ThreadPoolExecutor(1, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<>());*/
  public final TSMap<Class, String> typeSpecs = new TSMap<>();
  public final Object modulesLock = new Object();
  public final Cache cache;
  private final WeakHashMap<Thread, Object> threads = new WeakHashMap<>();
  /** Map cache path -> module */
  private final TSMap<ImportSpec, Module> modules = new TSMap<>();

  public CompilationContext(Path rootCachePath) {
    this.cache = new Cache(rootCachePath);
  }

  public static void processError(Module module, Throwable e) {
    if (e instanceof Common.UncheckedException) {
      processError(module, e.getCause());
    } else if (e instanceof InvocationTargetException) {
      processError(module, e.getCause());
    } else if (e instanceof MultiError) {
      module.log.errors.addAll(((MultiError) e).errors);
    } else {
      module.log.errors.add(Error.unexpected(module.id, e));
    }
  }

  public void loadRootModule(String path) {
    if (!Paths.get(path).isAbsolute()) throw new Assertion();
    LocalModuleId moduleId = new LocalModuleId(path);
    ImportSpec importSpec = new ImportSpec(moduleId);
    uncheck(
        () ->
            loadModule(
                    null,
                    null,
                    null,
                    importSpec,
                    new LoadModuleInner() {
                      @Override
                      public Value load(Module module) {
                        return compile(module, Paths.get(moduleId.path));
                      }
                    })
                .get());
  }

  private Value compile(Module module, Path sourcePath) {
    String className = Format.format("com.zarbosoft.alligatoroidmortar.Generated%s", uniqueClass++);
    // Do first pass flat evaluation
    MortarTargetModuleContext targetContext =
        new MortarTargetModuleContext(JVMDescriptor.jvmName(className));
    Context context = new Context(module, targetContext, new Scope(null));
    ROList<Value> rootStatements =
        new LanguageDeserializer(module.id).deserialize(module.log.errors, sourcePath);
    if (rootStatements == null) return ErrorValue.error;
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, null);
    MortarTargetModuleContext.LowerResult lowered =
        MortarTargetModuleContext.lower(
            context,
            ectx.record(
                new com.zarbosoft.alligatoroid.compiler.language.Scope(
                        null, new Block(null, rootStatements))
                    .evaluate(context)));
    EvaluateResult evaluateResult = ectx.build(null);
    MortarCode code =
        (MortarCode)
            targetContext.merge(
                context,
                null,
                new TSList<>(
                    evaluateResult.preEffect, lowered.valueCode, evaluateResult.postEffect));
    if (module.log.errors.some()) {
      return ErrorValue.error;
    }
    // Do 2nd pass jvm evaluation
    JVMSharedClass preClass = new JVMSharedClass(className);
    for (ROPair<Object, String> e : Common.iterable(targetContext.transfers.iterator())) {
      preClass.defineStaticField(e.second, e.first.getClass());
    }
    preClass.defineFunction(
        METHOD_NAME,
        JVMDescriptor.func(lowered.dataType.jvmDesc()),
        new MortarCode().add(code).add(lowered.dataType.returnOpcode()),
        new TSList<>());
    Class klass =
        DynamicClassLoader.loadTree(
            className, new TSMap<String, byte[]>().put(className, preClass.render()));
    for (ROPair<Object, String> e : Common.iterable(targetContext.transfers.iterator())) {
      uncheck(() -> klass.getDeclaredField(e.second).set(null, e.first));
    }
    return lowered.dataType.unlower(uncheck(() -> klass.getMethod(METHOD_NAME).invoke(null)));
  }

  public Future<Value> loadLocalModule(Context context, Location location, String path) {
    if (!Paths.get(path).isAbsolute()) throw new Assertion();
    LocalModuleId moduleId = new LocalModuleId(path);
    ImportSpec importSpec = new ImportSpec(moduleId);
    return loadModule(
        context.module.log,
        context.module.importPath,
        location,
        importSpec,
        new LoadModuleInner() {
          @Override
          public Value load(Module module) {
            Path sourcePath = Paths.get(moduleId.path);

            // Try to load from cache
            String sourceHash;
            {
              byte[] sourceBytes;
              try {
                sourceBytes = Files.readAllBytes(sourcePath);
              } catch (NoSuchFileException e) {
                module.log.errors.add(Error.deserializeMissingSourceFile(sourcePath));
                return ErrorValue.error;
              } catch (Throwable e) {
                processError(module, e);
                return ErrorValue.error;
              }
              sourceHash = new Utils.SHA256().add(sourceBytes).buildHex();
            }
            Path relCachePath = cache.ensureCachePath(module.log.warnings, moduleId);
            Path hashPath = cache.cachePath(relCachePath).resolve("hash");
            String outputHash =
                uncheck(
                    () -> {
                      try {
                        return Files.readString(hashPath);
                      } catch (NoSuchFileException e) {
                        return null;
                      }
                    });
            if (sourceHash.equals(outputHash)) {
              Value out = cache.loadOutput(module.log.warnings, relCachePath);
              if (out != ErrorValue.error) {
                return out;
              }
            }

            // Cache data bad - compile
            Value out = compile(module, Paths.get(moduleId.path));

            // Cache result
            if (out != ErrorValue.error) {
              cache.writeOutput(moduleId, module.log.warnings, relCachePath, out);
              try {
                Files.writeString(hashPath, sourceHash);
              } catch (Throwable e) {
                module.log.warnings.add(Error.unexpected(module.id, e));
              }
            }

            return out;
          }
        });
  }

  private Future<Value> loadModule(
      Log log,
      ImportPath fromImportPath,
      Location location,
      ImportSpec importSpec,
      LoadModuleInner inner) {
    synchronized (modulesLock) {
      if (fromImportPath != null) {
        TSList<ImportSpec> found = fromImportPath.find(new TSSet<>(),importSpec);
        if (found != null) {
          log.errors.add(Error.importLoop(location, found));
          CompletableFuture<Value> out = new CompletableFuture<>();
          out.complete(ErrorValue.error);
          return out;
        }
      }
      Module module = modules.getOpt(importSpec);
      if (module == null) {
        CompletableFuture<Value> result =
            new CompletableFuture<>() {
              ModuleId p = importSpec.moduleId;
            };
        ImportPath importPath = new ImportPath(importSpec);
        if (fromImportPath != null) importPath.from.add(fromImportPath);
        module = new Module(importSpec.moduleId, importPath, this, result);
        modules.put(importSpec, module);
        Module finalModule = module;
        Thread thread =
            new Thread(
                () -> {
                  try {
                    Value value = inner.load(finalModule);
                    finalModule.result.complete(value);
                  } catch (Throwable e) {
                    processError(finalModule, e);
                    finalModule.result.complete(null);
                  }
                });
        thread.start();
        synchronized (threads) {
          threads.put(thread, null);
        }
      } else {
        TSList<ImportSpec> found = module.importPath.findBefore(new TSSet<>(),importSpec);
        if (found != null) {
          log.errors.add(Error.importLoop(location, found));
          CompletableFuture<Value> out = new CompletableFuture<>();
          out.complete(ErrorValue.error);
          return out;
        }
        if (fromImportPath != null) {
          boolean inFrom = false;
          for (ImportPath seg : module.importPath.from) {
            if (seg.spec.equals(fromImportPath.spec)) {
              inFrom = true;
              break;
            }
          }
          if (!inFrom) module.importPath.from.add(fromImportPath);
        }
      }
      return module.result;
    }
  }

  public TSMap<ImportSpec, Module> join() {
    uncheck(
        () -> {
          List<Thread> joinThreads = new ArrayList<>();
          while (true) {
            joinThreads.clear();
            synchronized (threads) {
              joinThreads.addAll(threads.keySet());
            }
            if (joinThreads.isEmpty()) break;
            for (Thread thread : joinThreads) {
              thread.join();
            }
          }
        });
    return modules;
  }

  public Future<Value> loadRelativeModule(Context context, Location location, String path) {
    return context.module.id.dispatch(
        new ModuleId.Dispatcher<Future<Value>>() {
          @Override
          public Future<Value> handle(LocalModuleId id) {
            return loadLocalModule(
                context, location, Paths.get(id.path).resolveSibling(path).normalize().toString());
          }
        });
  }

  @FunctionalInterface
  interface LoadModuleInner {
    Value load(Module module);
  }
}
