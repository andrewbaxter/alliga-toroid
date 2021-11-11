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
import com.zarbosoft.alligatoroid.compiler.mortar.WholeValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ReverseIterable;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class CompilationContext {
  public static final String ENTRY_METHOD_NAME = "enter";
  public static final String METHOD_DESCRIPTOR = JVMDescriptor.func(JVMDescriptor.VOID_DESCRIPTOR);
  public static final String GENERATED_CLASS_PREFIX = "com.zarbosoft.alligatoroidmortar.Generated";
  public static long uniqueClass = 0;
  /*public final ExecutorService executor =
  new ThreadPoolExecutor(1, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<>());*/
  public final TSMap<Class, String> typeSpecs = new TSMap<>();
  public final Object modulesLock = new Object();
  public final Object remoteSourcesLock = new Object();
  public final Cache cache;
  public final TSMap<String, CompletableFuture<Path>> downloadMap = new TSMap<>();
  private final WeakHashMap<Thread, Object> threads = new WeakHashMap<>();
  /** Map cache path -> module */
  private final TSMap<ImportSpec, Module> modules = new TSMap<>();

  public CompilationContext(Path rootCachePath) {
    this.cache = new Cache(rootCachePath);
  }

  public static void processError(Module module, Throwable e) {
    if (e instanceof Common.UncheckedException) {
      processError(module, e.getCause());
    } else if (e instanceof ExecutionException) {
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
          module.log.errors.add(new Error.Unexpected(location, e));
        }
      } else {
        module.log.errors.add(new Error.PreDeserializeUnexpected(e));
      }
    }
  }

  public void loadRootModule(String path) {
    if (!Paths.get(path).isAbsolute()) throw new Assertion();
    CompletableFuture<Value> res = loadModule(null, null, new ImportSpec(new LocalModuleId(path)));
    uncheck(() -> res.get());
  }

  public <T> Value evaluate(
      Module module,
      ROList<Value> rootStatements,
      /** Only whole-ish values */
      ROOrderedMap<WholeValue, Value> initialScope) {
    String className = GENERATED_CLASS_PREFIX + uniqueClass++;
    // Do first pass flat evaluation
    MortarTargetModuleContext targetContext =
        new MortarTargetModuleContext(JVMDescriptor.jvmName(className));
    Context context = new Context(module, targetContext, new Scope(null));
    for (ROPair<WholeValue, Value> local : initialScope) {
      context.scope.put(local.first, local.second.bind(context, null).second);
    }
    EvaluateResult.Context ectx = new EvaluateResult.Context(context, null);
    MortarTargetModuleContext.LowerResult lowered =
        MortarTargetModuleContext.lower(
            context,
            ectx.record(
                new com.zarbosoft.alligatoroid.compiler.language.Scope(
                        null, new Block(null, rootStatements))
                    .evaluate(context)));
    EvaluateResult evaluateResult = ectx.build(null);
    for (CompletableFuture<Error> e : module.deferredErrors) {
      module.log.errors.add(uncheck(() -> e.get()));
    }
    MortarCode code = new MortarCode();
    code.add(
        targetContext.merge(
            context,
            null,
            new TSList<>(evaluateResult.preEffect, lowered.valueCode, evaluateResult.postEffect)));
    if (module.log.errors.some()) {
      return ErrorValue.error;
    }

    // Do 2nd pass jvm evaluation
    JVMSharedClass preClass = new JVMSharedClass(className);
    for (ROPair<Object, String> e : Common.iterable(targetContext.transfers.iterator())) {
      preClass.defineStaticField(e.second, e.first.getClass());
    }
    preClass.defineFunction(
        ENTRY_METHOD_NAME,
        JVMDescriptor.func(lowered.dataType.jvmDesc()),
        new MortarCode().add(code).add(lowered.dataType.returnOpcode()),
        new TSList<>());
    Class klass =
        DynamicClassLoader.loadTree(
            className, new TSMap<String, byte[]>().put(className, preClass.render()));
    for (ROPair<Object, String> e : Common.iterable(targetContext.transfers.iterator())) {
      uncheck(() -> klass.getDeclaredField(e.second).set(null, e.first));
    }
    Object pass2;
    try {
      pass2 = uncheck(() -> klass.getMethod(ENTRY_METHOD_NAME).invoke(null));
    } catch (Exception e) {
      processError(module, e);
      return ErrorValue.error;
    }
    return lowered.dataType.unlower(pass2);
  }

  public <T> Value evaluate(Module module, String path, InputStream source) {
    final ROList<Value> res =
        new LanguageDeserializer(module.spec.moduleId).deserialize(module.log.errors, path, source);
    if (res == null) {
      return ErrorValue.error;
    }
    return evaluate(module, res, ROOrderedMap.empty);
  }

  /**
   * Throws PreError or another Exception.
   *
   * @param module
   * @return
   */
  private Value loadModuleInner(Module module) {
    ImportSpec importSpec = module.spec;
    Path relCachePath = cache.ensureCachePath(importSpec.hash(), importSpec);

    Value out =
        module.spec.moduleId.dispatch(
            new ModuleId.Dispatcher<Value>() {
              @Override
              public Value handle(LocalModuleId id) {
                byte[] sourceBytes;
                try {
                  sourceBytes = uncheck(() -> Files.readAllBytes(Paths.get(id.path)));
                } catch (Common.UncheckedFileNotFoundException e) {
                  throw new Error.PreImportNotFound(id.path);
                }
                String sourceHash = new Utils.SHA256().add(sourceBytes).buildHex();
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

                // Try to load from cache
                if (sourceHash.equals(outputHash)) {
                  Value out = cache.loadOutput(module.log.warnings, relCachePath);
                  if (out != ErrorValue.error) {
                    return out;
                  }
                }

                // Write hash
                try {
                  Files.writeString(hashPath, sourceHash);
                } catch (Throwable e) {
                  module.log.warnings.add(new Error.PreDeserializeUnexpected(e));
                }

                // Cache data bad - compile
                Path path = Paths.get(id.path);
                return uncheck(
                    () -> {
                      try (InputStream stream = Files.newInputStream(path)) {
                        return evaluate(module, id.path, stream);
                      }
                    });
              }

              @Override
              public Value handle(RemoteModuleId id) {
                Path downloadPath;
                downloadPath = download(module.log.warnings, id.url, id.hash);
                if (downloadPath.toString().endsWith(".at")) {
                  return uncheck(
                      () -> {
                        try (InputStream stream = Files.newInputStream(downloadPath)) {
                          return evaluate(module, id.url, stream);
                        }
                      });
                } else if (downloadPath.toString().endsWith(".zip")) {
                  return new BundleValue(module.importPath, id, "");
                } else {
                  throw new Error.PreUnknownImportFileType(id.url);
                }
              }

              @Override
              public Value handle(BundleModuleSubId id) {
                Path bundlePath =
                    id.module.dispatch(
                        new ModuleId.Dispatcher<Path>() {
                          @Override
                          public Path handle(LocalModuleId id) {
                            return Paths.get(id.path);
                          }

                          @Override
                          public Path handle(RemoteModuleId id) {
                            return download(module.log.warnings, id.url, id.hash);
                          }

                          @Override
                          public Path handle(BundleModuleSubId id) {
                            throw new Assertion(); // Bundle bases are either local/remote, not sub
                          }
                        });
                return uncheck(
                    () -> {
                      try (ZipFile bundle = new ZipFile(bundlePath.toFile())) {
                        ZipEntry e = bundle.getEntry(id.path);
                        if (e == null) {
                          throw new Error.PreImportNotFound(id.toString());
                        }
                        if (e.isDirectory()) {
                          return new BundleValue(module.importPath, id.module, id.path);
                        }
                        if (id.path.endsWith(".at")) {
                          try (InputStream stream = Files.newInputStream(bundlePath)) {
                            return evaluate(module, id.toString(), stream);
                          }
                        } else {
                          throw new Error.PreUnknownImportFileType(id.toString());
                        }
                      }
                    });
              }
            });

    // Cache result
    if (out != ErrorValue.error) {
      cache.writeOutput(module.log.warnings, relCachePath, out);
    }

    return out;
  }

  private Path downloadInner(TSList<Error> warnings, String url, String hash) {
    final URL url1 = uncheck(() -> new URL(url));
    switch (url1.getProtocol()) {
      case "file":
        {
          Path downloadPath = Paths.get(url1.getPath());
          String downloadHash;
          try {
            downloadHash = new Utils.SHA256().add(downloadPath).buildHex();
          } catch (Common.UncheckedFileNotFoundException e) {
            throw new Error.PreImportNotFound(url);
          } catch (Exception e) {
            throw new Error.PreCacheUnexpected(downloadPath.toString(), e);
          }
          if (!downloadHash.equals(hash)) {
            throw new Error.PreRemoteModuleHashMismatch(url, hash, downloadHash);
          }
          return downloadPath;
        }
      case "http":
      case "https":
        {
          Path downloadDir =
              cache.ensureCachePath(
                  new Utils.SHA256().add(url).buildHex(), new TreeSerializable.Url(url));
          Path downloadPath = downloadDir.resolve(Paths.get(url1.getPath()).getFileName());

          // Check existing file
          {
            String downloadHash;
            do {
              try {
                downloadHash = new Utils.SHA256().add(downloadPath).buildHex();
              } catch (Common.UncheckedFileNotFoundException e) {
                // nop
                break;
              } catch (Exception e) {
                warnings.add(new Error.WarnUnexpected(downloadPath.toString(), e));
                break;
              }
              if (downloadHash.equals(hash)) {
                return downloadPath;
              }
            } while (false);
          }

          // No/bad existing file - download
          {
            String downloadHash =
                uncheck(
                    () -> {
                      OkHttpClient client = new OkHttpClient();
                      try (Response response =
                          client.newCall(new Request.Builder().get().build()).execute()) {
                        Files.copy(response.body().byteStream(), downloadPath);
                      }
                      return new Utils.SHA256().add(downloadPath).buildHex();
                    });
            if (!downloadHash.equals(hash)) {
              throw new Error.PreError() {
                @Override
                public Error toError(Location location) {
                  return new Error.RemoteModuleHashMismatch(location, url, hash, downloadHash);
                }
              };
            }
            return downloadPath;
          }
        }
      default:
        throw new Error.PreError() {
          @Override
          public Error toError(Location location) {
            return new Error.RemoteModuleProtocolUnsupported(location, url);
          }
        };
    }
  }

  public Path download(TSList<Error> warnings, String url, String hash) {
    CompletableFuture<Path> download;
    synchronized (downloadMap) {
      download = downloadMap.getOpt(url);
      if (download == null) {
        download = new CompletableFuture<>();
        downloadMap.put(url, download);
        CompletableFuture<Path> finalDownload = download;
        Thread downloadThread =
            new Thread(
                () -> {
                  Path downloadPath;
                  try {
                    downloadPath = downloadInner(warnings, url, hash);
                  } catch (Exception e) {
                    finalDownload.completeExceptionally(e);
                    return;
                  }
                  finalDownload.complete(downloadPath);
                });
        downloadThread.start();
        synchronized (threads) {
          threads.put(downloadThread, null);
        }
      }
    }
    CompletableFuture<Path> finalDownload = download;
    return uncheck(() -> finalDownload.get()); // TODO preerror
  }

  /**
   * Future always resolves with value, no exceptions.
   *
   * @param fromImportPath
   * @param importSpec
   * @return
   */
  public CompletableFuture<Value> loadModule(
      ROPair<Location, Module> loadFrom, ImportPath fromImportPath, ImportSpec importSpec) {
    CompletableFuture<Error> deferredError;
    if (loadFrom != null) {
      deferredError = new CompletableFuture<>();
      loadFrom.second.deferredErrors.add(deferredError);
    } else {
      deferredError = null;
    }
    synchronized (modulesLock) {
      Module module = modules.getOpt(importSpec);
      if (module == null) {
        CompletableFuture<Value> result = new CompletableFuture<>();

        if (fromImportPath != null) {
          TSList<ImportSpec> found = fromImportPath.find(new TSSet<>(), importSpec);
          if (found != null) {
            result.completeExceptionally(new ImportErrorLoop(found));
          }
        }

        ImportPath importPath = new ImportPath(importSpec);
        if (fromImportPath != null) importPath.from.add(fromImportPath);
        module = new Module(importSpec, importPath, this, result);
        modules.put(importSpec, module);
        Module finalModule = module;
        Thread thread =
            new Thread(
                () -> {
                  try {
                    final Value res = loadModuleInner(finalModule);
                    if (deferredError != null)
                      if (res == ErrorValue.error) {
                        deferredError.complete(Error.importError.toError(loadFrom.first));
                      } else {
                        deferredError.complete(null);
                      }
                    finalModule.result.complete(res);
                    return;
                  } catch (Error.PreError e) {
                    if (deferredError != null) {
                      deferredError.complete(e.toError(loadFrom.first));
                    } else {
                      finalModule.log.errors.add(new Error.PreDeserializeUnexpected(e));
                    }
                  } catch (Throwable e) {
                    if (deferredError != null) {
                      deferredError.complete(new Error.Unexpected(loadFrom.first, e));
                    } else {
                      finalModule.log.errors.add(new Error.PreDeserializeUnexpected(e));
                    }
                  }
                  finalModule.result.complete(ErrorValue.error);
                });
        thread.start();
        synchronized (threads) {
          threads.put(thread, null);
        }
      } else {
        TSList<ImportSpec> found = module.importPath.findBefore(new TSSet<>(), importSpec);
        if (found != null) {
          CompletableFuture<Value> result = new CompletableFuture<>();
          result.completeExceptionally(new ImportErrorLoop(found));
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

  public static class ImportErrorLoop extends Error.PreError {
    public final TSList<ImportSpec> loop;

    public ImportErrorLoop(TSList<ImportSpec> loop) {
      this.loop = loop;
    }

    @Override
    public Error toError(Location location) {
      return new Error.ImportLoop(location, loop);
    }
  }
}
