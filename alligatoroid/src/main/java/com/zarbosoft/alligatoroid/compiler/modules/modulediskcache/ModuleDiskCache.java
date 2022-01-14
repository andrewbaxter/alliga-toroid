package com.zarbosoft.alligatoroid.compiler.modules.modulediskcache;

import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemideserializeSemiserial;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Deserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.StateRecord;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.LocationlessUnexpected;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.modules.Module;
import com.zarbosoft.alligatoroid.compiler.modules.ModuleResolver;
import com.zarbosoft.alligatoroid.compiler.modules.Source;
import com.zarbosoft.luxem.read.path.LuxemArrayPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class ModuleDiskCache implements ModuleResolver {
  public static final String SUBDIR_RESULT = "result";
  public static final String SUBDIR_SOURCE = "source";
  public static final String CACHE_FILENAME_ID = "id.luxem";
  public static final String CACHE_FILENAME_OUTPUT = "output.luxem";
  public static final String CACHE_DIRECTORY_ARTIFACTS = "artifacts";
  public static final String CACHE_SUBARTIFACT_TYPE_STRING = "string";
  public static final String CACHE_SUBARTIFACT_TYPE_INT = "int";
  public static final String CACHE_SUBARTIFACT_TYPE_BOOL = "bool";
  public static final String CACHE_SUBARTIFACT_TYPE_BUILTIN = "builtin";
  public static final String CACHE_SUBARTIFACT_TYPE_CACHE = "cache";
  public static final String CACHE_SUBARTIFACT_TYPE_NULL = "null";
  public final Object cacheDirLock = new Object();
  public final Path rootCachePath;
  public final ModuleResolver inner;

  public ModuleDiskCache(Path rootCachePath, ModuleResolver inner) {
    this.rootCachePath = rootCachePath;
    this.inner = inner;
  }

  @Override
  public Module get(
      CompileContext context, ImportPath fromImportPath, ImportId importId, Source source) {
    // Find the location the result would be written
    Path cachePath = null;
    Path hashPath = null;
    do {
      if (context.localDirty.contains(importId.moduleId)) {
        break;
      }
      try {
        cachePath =
            uncheck(
                () -> {
                  synchronized (cacheDirLock) {
                    byte[] wantIdBytes;
                    {
                      ByteArrayOutputStream wantIdBytes1 = new ByteArrayOutputStream();
                      Writer writer = new Writer(wantIdBytes1, (byte) ' ', 4);
                      importId.treeSerialize(writer);
                      wantIdBytes = wantIdBytes1.toByteArray();
                    }

                    return Utils.uniqueDir(rootCachePath.resolve(SUBDIR_RESULT), wantIdBytes);
                  }
                });
        hashPath = cachePath.resolve("hash");
        String outputHash = Files.readString(hashPath);
        if (source.hash.equals(outputHash)) {
          TSList<Error> deserializeErrors = new TSList<>();
          final SemideserializeSemiserial rootState =
              new SemideserializeSemiserial(new LuxemArrayPathBuilder(null));
          Deserializer.deserialize(
              null, deserializeErrors, cachePath, new TSList<>(new StateRecord(rootState)));
          SemiserialModule res = rootState.build(null, deserializeErrors);
          if (deserializeErrors.some()) {
            for (Error e : deserializeErrors) {
              context.logger.warn(e);
            }
            break;
          }
          if (res == null)
            throw new Assertion(); // Can this happen? no errors, no exception, but dead return
          return new Module() {
            @Override
            public ImportId spec() {
              return importId;
            }

            @Override
            public SemiserialModule result() {
              return res;
            }
          };
        }
      } catch (Exception e) {
        context.logger.warn(new LocationlessUnexpected(e));
      }
    } while (false);

    // Cache data bad - compile
    Module out = inner.get(context, fromImportPath, importId, source);

    // Cache result, source mappings, other things
    if (cachePath != null) {
      try {
        Files.writeString(hashPath, source.hash);
      } catch (Throwable e) {
        context.logger.warn(new LocationlessUnexpected(e));
      }
      Path outputPath = cachePath.resolve(CACHE_FILENAME_OUTPUT);
      Utils.recursiveDelete(outputPath);
      final Path artifactDir = cachePath.resolve(CACHE_DIRECTORY_ARTIFACTS);
      Utils.recursiveDelete(artifactDir);
      uncheck(() -> Files.createDirectory(artifactDir));
      try (OutputStream stream = Files.newOutputStream(outputPath)) {
        Writer writer = new Writer(stream, (byte) ' ', 4);
        out.result().treeSerialize(writer);
      } catch (Throwable e) {
        context.logger.warn(new LocationlessUnexpected(e));
      }
    }

    return out;
  }
}
