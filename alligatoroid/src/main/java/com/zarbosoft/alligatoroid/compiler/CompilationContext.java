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
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class CompilationContext {
  public static final String METHOD_NAME = "enter";
  public static final String METHOD_DESCRIPTOR = JVMDescriptor.func(JVMDescriptor.VOID_DESCRIPTOR);
  public static final String GENERATED_CLASS_PREFIX = "com.zarbosoft.alligatoroidmortar.Generated";
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
    } else if (e instanceof MultiError) {
      module.log.errors.addAll(((MultiError) e).errors);
    } else {
      Location location = null; // TODO convert whole stack?
      for (StackTraceElement t : new ReverseIterable<>(Arrays.asList(e.getStackTrace()))) {
        if (t.getClassName().startsWith(GENERATED_CLASS_PREFIX)) {
          location = module.sourceMapReverse.get(t.getLineNumber());
          break;
        }
      }
      if (location != null) {
        if (e instanceof Error.PreError) {
          module.log.errors.add(((Error.PreError) e).toError(location));
        } else {
          module.log.errors.add(Error.unexpectedAt(location, e));
        }
      } else {
        module.log.errors.add(Error.unexpected(e));
      }
    }
  }

  public void loadRootModule(String path) {
    if (!Paths.get(path).isAbsolute()) throw new Assertion();
    ImportResult res = loadModule(null, new ImportSpec(new LocalModuleId(path)));
    if (res.error != null) throw new Assertion();
    uncheck(() -> res.value.get());
  }

  private <T> Value compile(Module module, Path sourcePath) {
    String className = GENERATED_CLASS_PREFIX + uniqueClass++;
    // Do first pass flat evaluation
    MortarTargetModuleContext targetContext =
        new MortarTargetModuleContext(JVMDescriptor.jvmName(className));
    Context context = new Context(module, targetContext, new Scope(null));
    ROList<Value> rootStatements =
        new LanguageDeserializer(module.spec.moduleId).deserialize(module.log.errors, sourcePath);
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

  private Value loadModuleInner(Module module) {
    ImportSpec importSpec = module.spec;

    // Try to load from cache
    String sourceHash =
        importSpec.moduleId.dispatch(
            new ModuleId.Dispatcher<String>() {
              @Override
              public String handle(LocalModuleId id) {
                Path sourcePath = Paths.get(id.path);
                byte[] sourceBytes;
                try {
                  sourceBytes = Files.readAllBytes(sourcePath);
                } catch (NoSuchFileException e) {
                  module.log.errors.add(Error.deserializeMissingSourceFile(sourcePath));
                  return null;
                } catch (Throwable e) {
                  processError(module, e);
                  return null;
                }
                return new Utils.SHA256().add(sourceBytes).buildHex();
              }
            });
    if (sourceHash == null) return ErrorValue.error;
    Path relCachePath = cache.ensureCachePath(module.log.warnings, importSpec);
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
    Value out =
        importSpec.moduleId.dispatch(
            new ModuleId.Dispatcher<Value>() {
              @Override
              public Value handle(LocalModuleId id) {
                return compile(module, Paths.get(id.path));
              }
            });

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

  public ImportResult loadModule(ImportPath fromImportPath, ImportSpec importSpec) {
    synchronized (modulesLock) {
      if (fromImportPath != null) {
        TSList<ImportSpec> found = fromImportPath.find(new TSSet<>(), importSpec);
        if (found != null) {
          return ImportResult.err(new ImportErrorLoop(found));
        }
      }
      Module module = modules.getOpt(importSpec);
      if (module == null) {
        CompletableFuture<Value> result = new CompletableFuture<>();
        ImportPath importPath = new ImportPath(importSpec);
        if (fromImportPath != null) importPath.from.add(fromImportPath);
        module = new Module(importSpec, importPath, this, result);
        modules.put(importSpec, module);
        Module finalModule = module;
        Thread thread =
            new Thread(
                () -> {
                  try {
                    Value value = loadModuleInner(finalModule);
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
        TSList<ImportSpec> found = module.importPath.findBefore(new TSSet<>(), importSpec);
        if (found != null) {
          return ImportResult.err(new ImportErrorLoop(found));
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
      return ImportResult.ok(module.result);
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

  public static class ImportResult {
    public final Error.PreError error;
    public final Future<Value> value;

    private ImportResult(Error.PreError error, Future<Value> value) {
      this.error = error;
      this.value = value;
    }

    public static ImportResult ok(Future<Value> value) {
      return new ImportResult(null, value);
    }

    public static ImportResult err(Error.PreError error) {
      return new ImportResult(error, null);
    }
  }

  public static class ImportErrorLoop extends Error.PreError {
    public final TSList<ImportSpec> loop;

    public ImportErrorLoop(TSList<ImportSpec> loop) {
      this.loop = loop;
    }

    @Override
    public Error toError(Location location) {
      return Error.importLoop(location, loop);
    }
  }
}
