package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.alligatoroid.compiler.modules.Logger;
import com.zarbosoft.appdirsj.AppDirs;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROSetRef;
import com.zarbosoft.rendaw.common.TSList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Alligatorus {
  public static final AppDirs appDirs =
      new AppDirs().set_appname("alligatoroid").set_appauthor("zarbosoft");

  public static ImportId rootModuleSpec(Path path) {
    path = path.toAbsolutePath().normalize();
    return ImportId.create(LocalModuleId.create(path.toString()));
  }

  public static Path defaultCachePath() {
    String cachePath0 = System.getenv("ALLIGATOROID_CACHE");
    if (cachePath0 == null || cachePath0.isEmpty()) {
      return appDirs.user_cache_dir(false);
    } else {
      return Paths.get(cachePath0);
    }
  }

  public static Result compile(Path cachePath, Logger logger, ImportId spec) {
    RootModuleId rootModuleId = new RootModuleId();
    final ImportId rootImportId = ImportId.create(rootModuleId);
    final Location rootLocation = Location.create(rootModuleId, 0);
    CompileContext context = new CompileContext(cachePath, logger, spec);
    ModuleCompileContext moduleContext = new ModuleCompileContext(rootImportId, context, null);
    context.moduleErrors.put(rootImportId, moduleContext.errors);
    try {
      try {
        moduleContext.getModule(spec).get();
      } catch (ExecutionException e) {
        throw e.getCause();
      }
    } catch (InterruptedException e) {
      context.threads.interrupt();
      throw uncheck(e);
    } catch (Error.PreError e) {
      context.moduleErrors.put(rootImportId, new TSList<>(e.toError(rootLocation)));
    } catch (Throwable e) {
      context.moduleErrors.put(rootImportId, new TSList<>(new Unexpected(rootLocation, e)));
    } finally {
      context.threads.join();
    }
    return new Result(
        context.moduleErrors, context.localSources, (Map) context.traceModuleStringFields);
  }

  public static class Result {
    public final Map<ImportId, ROList<Error>> errors;
    public final Map<ModuleId, Path> localSources;
    public final Map<ModuleId, ROMap<Location, ROSetRef<String>>> traceStringFields;

    public Result(
        Map<ImportId, ROList<Error>> errors,
        Map<ModuleId, Path> localSources,
        Map<ModuleId, ROMap<Location, ROSetRef<String>>> traceStringFields) {
      this.errors = errors;
      this.localSources = localSources;
      this.traceStringFields = traceStringFields;
    }
  }
}
