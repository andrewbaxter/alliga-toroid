package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.cache.Cache;
import com.zarbosoft.alligatoroid.compiler.jvm.MultiError;
import com.zarbosoft.alligatoroid.compiler.jvmshared.DynamicClassLoader;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedClass;
import com.zarbosoft.alligatoroid.compiler.language.Block;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.sourcedeserialize.SourceDeserializer;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class CompilationContext {
  public static final String METHOD_NAME = "enter";
  public static final String METHOD_DESCRIPTOR = JVMDescriptor.func(JVMDescriptor.voidDescriptor());
  public static long uniqueClass = 0;
  public final ExecutorService executor = Executors.newWorkStealingPool();
  public final TSMap<Class, String> typeSpcs = new TSMap<>();

  public final Object modulesLock = new Object();
  public final Cache cache;
  /** Map cache path -> module */
  private final TSMap<ImportSpec, Module> modules = new TSMap<>();

  private final ConcurrentLinkedDeque<Future> newPending = new ConcurrentLinkedDeque<>();

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
      module.log.errors.add(Error.unexpected(e));
    }
  }

  public void loadRootModule(String path) {
    LocalModuleId moduleId = new LocalModuleId(path);
    ImportSpec importSpec = new ImportSpec(moduleId);
    uncheck(
        () ->
            loadModule(
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
        new SourceDeserializer(module.id).deserialize(module.log.errors, sourcePath);
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

  public Future<Value> loadLocalModule(String path) {
    LocalModuleId moduleId = new LocalModuleId(path);
    ImportSpec importSpec = new ImportSpec(moduleId);
    return loadModule(
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
              cache.writeOutput(module.log.warnings, relCachePath, out);
              try {
                Files.writeString(hashPath, sourceHash);
              } catch (Throwable e) {
                module.log.warnings.add(Error.unexpected(e));
              }
            }

            return out;
          }
        });
  }

  private Future<Value> loadModule(ImportSpec importSpec, LoadModuleInner inner) {
    synchronized (modulesLock) {
      Module module = modules.getOpt(importSpec);
      if (module == null) {
        CompletableFuture<Value> result = new CompletableFuture<>();
        module = new Module(importSpec.moduleId, this, result);
        modules.put(importSpec, module);
        Module finalModule = module;
        executor.submit(
            () -> {
              try {
                Value value = inner.load(finalModule);
                finalModule.result.complete(value);
              } catch (Throwable e) {
                processError(finalModule, e);
                finalModule.result.complete(null);
              }
            });
        newPending.add(module.result);
      }
      return module.result;
    }
  }

  public TSMap<ImportSpec, Module> join() {
    uncheck(
        () -> {
          try {
            while (true) {
              Future got = newPending.pollLast();
              if (got == null) break;
              got.get();
            }
          } finally {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
          }
        });
    return modules;
  }

  public Future<Value> loadRelativeModule(ModuleId id, String path) {
    return id.dispatch(
        new ModuleId.Dispatcher<Future<Value>>() {
          @Override
          public Future<Value> handle(LocalModuleId id) {
            return loadLocalModule(Paths.get(id.path).resolveSibling(path).normalize().toString());
          }
        });
  }

  @FunctionalInterface
  interface LoadModuleInner {
    Value load(Module module);
  }
}
