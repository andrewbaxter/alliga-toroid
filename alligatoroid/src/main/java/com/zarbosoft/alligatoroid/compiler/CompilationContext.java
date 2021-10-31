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
import java.util.concurrent.Future;
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
    ImportResult res = loadModule(null, new ImportSpec(new LocalModuleId(path)));
    if (res.error != null) throw new Assertion();
    uncheck(() -> res.value.get());
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
    return lowered.dataType.unlower(uncheck(() -> klass.getMethod(ENTRY_METHOD_NAME).invoke(null)));
  }

  public <T> Value evaluate(Module module, String path, InputStream source) {
    ROList<Value> rootStatements =
        new LanguageDeserializer(module.spec.moduleId).deserialize(module.log.errors, path, source);
    if (rootStatements == null) return ErrorValue.error;
    return evaluate(module, rootStatements, ROOrderedMap.empty);
  }

  private Value loadModuleInner(Module module) {
    ImportSpec importSpec = module.spec;
    Path relCachePath = cache.ensureCachePath(module.log.warnings, importSpec.hash(), importSpec);

    Value out =
        module.spec.moduleId.dispatch(
            new ModuleId.Dispatcher<Value>() {
              @Override
              public Value handle(LocalModuleId id) {
                ImportSpec importSpec = module.spec;
                Path relCachePath =
                    cache.ensureCachePath(module.log.warnings, importSpec.hash(), importSpec);
                byte[] sourceBytes;
                try {
                  sourceBytes = Files.readAllBytes(Paths.get(id.path));
                } catch (NoSuchFileException e) {
                  module.log.errors.add(new Error.DeserializeMissingSourceFile());
                  return ErrorValue.error;
                } catch (Throwable e) {
                  processError(module, e);
                  return ErrorValue.error;
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
                    return ErrorValue.error;
                  }
                }

                // Cache data bad - compile
                Value out;
                Path path = Paths.get(id.path);
                try (InputStream stream = Files.newInputStream(path)) {
                  out = evaluate(module, id.path, stream);
                } catch (Exception e) {
                  module.log.warnings.add(new Error.CacheUnexpected(id.path, e));
                  out = ErrorValue.error;
                }

                // Write hash
                try {
                  Files.writeString(hashPath, sourceHash);
                } catch (Throwable e) {
                  module.log.warnings.add(new Error.PreDeserializeUnexpected(e));
                }

                return out;
              }

              @Override
              public Value handle(RemoteModuleId id) {
                Path downloadPath;
                try {
                  downloadPath = download(module.log.warnings, id.url, id.hash);
                } catch (DownloadHashMismatch e) {
                  module.log.errors.add(
                          new Error.RemoteModuleHashMismatch(id.url, id.hash, e.downloadHash));
                  return ErrorValue.error;
                }
                if (downloadPath.toString().endsWith(".at")) {
                  try (InputStream stream = Files.newInputStream(downloadPath)) {
                    return evaluate(module, id.url, stream);
                  } catch (Exception e) {
                    module.log.warnings.add(new Error.CacheUnexpected(id.url, e));
                    return ErrorValue.error;
                  }
                } else if (downloadPath.toString().endsWith(".zip")) {
                  return new BundleValue(module.importPath, id, "");
                } else {
                  module.log.errors.add(new Error.UnknownImportFileType());
                  return ErrorValue.error;
                }
              }

              @Override
              public Value handle(RemoteModuleSubId id) {
                Path downloadPath;
                try {
                  downloadPath = download(module.log.warnings, id.module.url, id.module.hash);
                } catch (DownloadHashMismatch e) {
                  module.log.errors.add(
                          new Error.RemoteModuleHashMismatch(id.module.url, id.module.hash, e.downloadHash));
                  return ErrorValue.error;
                }
                return uncheck(
                    () -> {
                      try (ZipFile bundle = new ZipFile(downloadPath.toFile())) {
                        ZipEntry e = bundle.getEntry(id.path);
                        String compPath = id.module.url + "/" + id.path;
                        if (e == null) {
                          module.log.errors.add(new Error.DeserializeMissingSourceFile());
                          return ErrorValue.error;
                        } else if (e.isDirectory()) {
                          return new BundleValue(module.importPath, id.module, id.path);
                        } else if (id.path.endsWith(".at")) {
                          try (InputStream stream = Files.newInputStream(downloadPath)) {
                            return evaluate(module, compPath, stream);
                          } catch (Exception e2) {
                            module.log.warnings.add(new Error.CacheUnexpected(compPath, e2));
                            return ErrorValue.error;
                          }
                        } else {
                          module.log.errors.add(new Error.UnknownImportFileType());
                          return ErrorValue.error;
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

  public Path download(TSList<Error> warnings, String url, String hash) {
    CompletableFuture<Path> download;
    synchronized (downloadMap) {
      download = downloadMap.get(url);
      if (download == null) {
        download = new CompletableFuture<>();
        downloadMap.put(url, download);

        Path downloadDir =
            cache.ensureCachePath(
                warnings, new Utils.SHA256().add(url).buildHex(), new TreeSerializable.Url(url));
        Path urlPath = uncheck(() -> Paths.get(new URL(url).getPath()));
        Path downloadPath = downloadDir.resolve(urlPath.getFileName());

        // Check existing file
        {
          String downloadHash;
          try {
            downloadHash = new Utils.SHA256().add(downloadPath).buildHex();
            if (downloadHash.equals(hash)) {
              download.complete(downloadPath);
              return downloadPath;
            }
          } catch (Common.UncheckedFileNotFoundException e) {
            // nop
          } catch (Exception e) {
            warnings.add(new Error.CacheUnexpected(downloadPath.toString(), e));
          }
        }

        // No/bad existing file - download
        CompletableFuture<Path> finalDownload = download;
        Thread downloadThread =
            new Thread(
                () -> {
                  String downloadHash;
                  try {
                    OkHttpClient client = new OkHttpClient();
                    try (Response response =
                        client.newCall(new Request.Builder().get().build()).execute()) {
                      Files.copy(response.body().byteStream(), downloadPath);
                    }
                    downloadHash = new Utils.SHA256().add(downloadPath).buildHex();
                  } catch (Exception e) {
                    finalDownload.completeExceptionally(e);
                    return;
                  }
                  if (!downloadHash.equals(hash)) {
                    finalDownload.completeExceptionally(new DownloadHashMismatch(downloadHash));
                  } else {
                    finalDownload.complete(downloadPath);
                  }
                });
        downloadThread.start();
        synchronized (threads) {
          threads.put(downloadThread, null);
        }
      }
    }
    CompletableFuture<Path> finalDownload = download;
    return uncheck(() -> finalDownload.get());
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
                    finalModule.result.complete(loadModuleInner(finalModule));
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

  public static class DownloadHashMismatch extends RuntimeException {
    private final String downloadHash;

    public DownloadHashMismatch(String downloadHash) {
      this.downloadHash = downloadHash;
    }
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
      return new Error.ImportLoop(location, loop);
    }
  }
}
