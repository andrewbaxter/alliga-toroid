package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.appdirsj.AppDirs;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.nio.file.Path;
import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class Alligatorus {
  public static final AppDirs appDirs =
      new AppDirs().set_appname("alligatoroid").set_appauthor("zarbosoft");

  public static ImportId rootModuleSpec(Path path) {
    path = path.toAbsolutePath().normalize();
    return new ImportId(new LocalModuleId(path.toString()));
  }

  public static Result compile(Path cachePath, ImportId spec) {
    RootModuleId rootModuleId = new RootModuleId();
    final ImportId rootImportId = new ImportId(rootModuleId);
    CompileContext context = new CompileContext(cachePath, spec);
    ModuleCompileContext moduleContext = new ModuleCompileContext(rootImportId, context, null);
    context.moduleErrors.put(rootImportId, moduleContext.errors);
    try {
      uncheck(() -> context.modules.get(moduleContext, spec).get());
    } catch (Error.PreLocationlessError e) {
      context.moduleErrors.put(rootImportId, new TSList<>(e.toError()));
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
