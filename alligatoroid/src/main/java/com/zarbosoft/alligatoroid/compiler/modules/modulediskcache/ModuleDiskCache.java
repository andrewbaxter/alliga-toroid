package com.zarbosoft.alligatoroid.compiler.modules.modulediskcache;

import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialModule;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.BaseStateSingle;
import com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.Deserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.AutoTreeMeta;
import com.zarbosoft.alligatoroid.compiler.inout.utils.treeauto.TypeInfo;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WarnUnexpected;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
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
  private static final AutoTreeMeta semisubMeta;
  private static final AutoTreeMeta importIdMeta;

  static {
    semisubMeta = new AutoTreeMeta();
    semisubMeta.scan(SemiserialModule.class);
    importIdMeta = new AutoTreeMeta();
    importIdMeta.scan(ImportId.class);
  }

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
    Path outputPath = null;
    do {
      if (importId.moduleId.dispatch(
          new ModuleId.DefaultDispatcher<Boolean>(false) {
            @Override
            public Boolean handleLocal(LocalModuleId id) {
              return context.dependents.isDirty(id.path);
            }
          })) break;
      try {
        cachePath =
            uncheck(
                () -> {
                  synchronized (cacheDirLock) {
                    byte[] wantIdBytes;
                    {
                      ByteArrayOutputStream wantIdBytes1 = new ByteArrayOutputStream();
                      Writer writer = new Writer(wantIdBytes1, (byte) ' ', 4);
                      importIdMeta.serialize(writer, TypeInfo.fromClass(ImportId.class), importId);
                      wantIdBytes = wantIdBytes1.toByteArray();
                    }

                    return Utils.uniqueDir(rootCachePath.resolve("result"), wantIdBytes);
                  }
                });
        hashPath = cachePath.resolve("hash");
        outputPath = cachePath.resolve("output.luxem");
        String outputHash = Files.readString(hashPath);
        if (source.hash.equals(outputHash)) {
          TSList<Error> deserializeErrors = new TSList<>();
          final BaseStateSingle<Void, SemiserialModule> rootState =
              semisubMeta.deserialize(
                  deserializeErrors, new LuxemArrayPathBuilder(null), SemiserialModule.class);
          Deserializer.deserialize(null, deserializeErrors, outputPath, new TSList<>(rootState));
          SemiserialModule res = rootState.build(null, deserializeErrors);
          if (deserializeErrors.some()) {
            for (Error e : deserializeErrors) {
              context.logger.warn(e);
            }
            break;
          }
          if (res == null)
            throw new Assertion(); // Can this happen? no errors, no exception, but dead return

          // Validate by desemiserializing once
          new ModuleCompileContext(null, context, null).desemiserialize(res, importId);

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
      } catch (Error.PreError e) {
        context.logger.warn(e);
      } catch (Exception e) {
        context.logger.warn(new WarnUnexpected(source.path.toString(), e));
      }
    } while (false);

    // Cache data bad - compile
    Module out = inner.get(context, fromImportPath, importId, source);

    // Cache result, source mappings, other things
    if (cachePath != null) {
      try {
        Files.writeString(hashPath, source.hash);
      } catch (Throwable e) {
        context.logger.warn(new WarnUnexpected(source.path.toString(), e));
      }
      Utils.recursiveDelete(outputPath);
      try (OutputStream stream = Files.newOutputStream(outputPath)) {
        Writer writer = new Writer(stream, (byte) ' ', 4);
        semisubMeta.serialize(writer, TypeInfo.fromClass(SemiserialModule.class), out.result());
      } catch (Throwable e) {
        context.logger.warn(new WarnUnexpected(source.path.toString(), e));
      }
    }

    return out;
  }
}
