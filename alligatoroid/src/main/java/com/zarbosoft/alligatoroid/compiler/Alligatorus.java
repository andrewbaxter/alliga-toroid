package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.appdirsj.AppDirs;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Alligatorus {
  public static final AppDirs appDirs =
      new AppDirs().set_appname("alligatoroid").set_appauthor("zarbosoft");

  public static ImportId rootModuleSpec(Path path) {
    path = path.toAbsolutePath().normalize();
    return new ImportId(new LocalModuleId(path.toString()));
  }

  public static Path defaultCachePath() {
    String cachePath0 = System.getenv("ALLIGATOROID_CACHE");
    if (cachePath0 == null || cachePath0.isEmpty()) {
      return appDirs.user_cache_dir(false);
    } else {
      return Paths.get(cachePath0);
    }
  }

  public static Result compile(Path cachePath, ImportId spec) {
    RootModuleId rootModuleId = new RootModuleId();
    final ImportId rootImportId = new ImportId(rootModuleId);
    final Location rootLocation = new Location(rootModuleId, 0);
    CompileContext context = new CompileContext(cachePath, spec);
    ModuleCompileContext moduleContext = new ModuleCompileContext(rootImportId, context, null);
    context.moduleErrors.put(rootImportId, moduleContext.errors);
    try {
      uncheck(() -> context.modules.get(moduleContext, spec).get());
    } catch (Error.PreLocationlessError e) {
      context.moduleErrors.put(rootImportId, new TSList<>(e.toError()));
    } catch (Error.PreError e) {
      context.moduleErrors.put(rootImportId, new TSList<>(e.toError(rootLocation)));
    } finally {
      context.threads.join();
    }
    return new Result(context.moduleErrors, context.localSources);
  }

  public static class Result {
    public final Map<ImportId, ROList<Error>> errors;
    public final Map<ModuleId, Path> localSources;

    public Result(Map<ImportId, ROList<Error>> errors, Map<ModuleId, Path> localSources) {
      this.errors = errors;
      this.localSources = localSources;
    }
  }
}
