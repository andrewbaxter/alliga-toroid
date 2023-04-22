package com.zarbosoft.alligatoroid.compiler.modules.modulediskcache;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zarbosoft.alligatoroid.compiler.CompileContext;
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
import com.zarbosoft.alligatoroid.compiler.modules.CacheImportIdRes;
import com.zarbosoft.alligatoroid.compiler.modules.Module;
import com.zarbosoft.alligatoroid.compiler.modules.ModuleResolver;
import com.zarbosoft.alligatoroid.compiler.modules.Source;
import com.zarbosoft.luxem.read.path.LuxemArrayPathBuilder;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
  public final ModuleResolver inner;
  private final Tables db;

  public ModuleDiskCache(Path rootCachePath, ModuleResolver inner) {
    uncheck(() -> Files.createDirectories(rootCachePath));
    final Path cachePath = rootCachePath.resolve("cache.sqlite3");

    int currentVersion = 1;
    db =
        new Supplier<Tables>() {
          @Override
          public Tables get() {
            return uncheck(
                () -> {
                  Tables tables = null;
                  try {
                    tables = new Tables(cachePath);
                    try (CloseableIterator<TableVersion> iter = tables.version.iterator()) {
                      if (iter.next().version == currentVersion) {
                        return tables;
                      }
                    }
                  } catch (Exception e) {
                    if (tables != null) {
                      tables.connectionSource.closeQuietly();
                    }
                    // TODO warn properly (logger/compile context)
                    System.out.format("DEBUG failed to get cache version: %s", e);
                  }
                  try {
                    Files.delete(cachePath);
                  } catch (NoSuchFileException e) {
                    // nop
                  }
                  tables = new Tables(cachePath);
                  TableUtils.createTable(tables.version);
                  tables.version.create(TableVersion.create(currentVersion));
                  TableUtils.createTable(tables.modules);
                  return tables;
                });
          }
        }.get();
    this.inner = inner;
  }

  @Override
  public CacheImportIdRes getCacheId(ImportId id) {
    return uncheck(
        () -> {
          byte[] wantIdBytes;
          {
            ByteArrayOutputStream wantIdBytes1 = new ByteArrayOutputStream();
            Writer writer = new Writer(wantIdBytes1, (byte) ' ', 4);
            importIdMeta.serialize(writer, TypeInfo.fromClass(ImportId.class), id);
            wantIdBytes = wantIdBytes1.toByteArray();
          }
          final QueryBuilder<TableModule, Long> query = db.modules.queryBuilder();
          query.limit(1l);
          query.where().eq("spec", wantIdBytes);
          final List<TableModule> res = query.query();
          if (res.size() > 0) {
            return new CacheImportIdRes(id, res.get(0).id);
          }
          final TableModule mod = new TableModule();
          mod.spec = wantIdBytes;
          db.modules.create(mod);
          return new CacheImportIdRes(id, mod.id);
        });
  }

  @Override
  public Module get(
      CompileContext context, ImportPath fromImportPath, CacheImportIdRes cacheId, Source source) {
    // Find the location the result would be written
    TableModule tableMod = null;
    do {
      if (cacheId.importId.moduleId.dispatch(
          new ModuleId.DefaultDispatcher<Boolean>(false) {
            @Override
            public Boolean handleLocal(LocalModuleId id) {
              return context.dependents.isDirty(id.path);
            }
          })) {
        break;
      }
      try {
        tableMod = uncheck(() -> db.modules.queryForId(cacheId.cacheId));
        if (Arrays.equals(source.hash.getBytes(StandardCharsets.UTF_8), tableMod.outputHash)) {
          TSList<Error> deserializeErrors = new TSList<>();
          final BaseStateSingle<Void, SemiserialModule> rootState =
              semisubMeta.deserialize(
                  deserializeErrors, new LuxemArrayPathBuilder(null), SemiserialModule.class);
          Deserializer.deserialize(
              null,
              deserializeErrors,
              "cache output " + cacheId.cacheId,
              new ByteArrayInputStream(tableMod.output),
              new TSList<>(rootState));
          SemiserialModule res = rootState.build(null, deserializeErrors);
          if (deserializeErrors.some()) {
            for (Error e : deserializeErrors) {
              context.logger.warn(e);
            }
            break;
          }
          if (res == null) {
            throw new Assertion(); // Can this happen? no errors, no exception, but dead return
          }

          // Validate by desemiserializing once
          // new ModuleCompileContext(null, context, null).desemiserialize(res, importId);

          return new Module() {
            @Override
            public long cacheId() {
              return cacheId.cacheId;
            }

            @Override
            public ImportId spec() {
              return cacheId.importId;
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
    Module out = inner.get(context, fromImportPath, cacheId, source);

    // Cache result, source mappings, other things
    if (tableMod != null) {
      try {
        tableMod.outputHash = source.hash.getBytes(StandardCharsets.UTF_8);
        final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Writer writer = new Writer(outputBytes, (byte) ' ', 4);
        semisubMeta.serialize(writer, TypeInfo.fromClass(SemiserialModule.class), out.result());
        tableMod.output = outputBytes.toByteArray();
        db.modules.update(tableMod);
      } catch (Throwable e) {
        context.logger.warn(new WarnUnexpected(source.path.toString(), e));
      }
    }
    return out;
  }

  public static class Tables {
    public final ConnectionSource connectionSource;
    public final Dao<TableVersion, Void> version;
    public final Dao<TableModule, Long> modules;

    public Tables(Path path) {
      this.connectionSource = uncheck(() -> new JdbcConnectionSource("jdbc:sqlite:" + path));
      this.version = uncheck(() -> DaoManager.createDao(connectionSource, TableVersion.class));
      this.modules = uncheck(() -> DaoManager.createDao(connectionSource, TableModule.class));
    }
  }
}
