package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RootModuleId;
import com.zarbosoft.appdirsj.AppDirs;
import com.zarbosoft.rendaw.common.ROList;

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
    CompileContext context = new CompileContext(cachePath);
    ModuleCompileContext moduleContext =
        new ModuleCompileContext(rootImportId, context, new ImportPath(null));
    context.moduleErrors.put(rootImportId, moduleContext.errors);
    try {
      uncheck(() -> context.modules.get(moduleContext, new ImportPath(null), spec).get());
    } finally {
      context.threads.join();
    }
    return new Result(context.moduleErrors, context.localSources);
  }

  public static class Result {
    public final Map<ImportId, ROList<Error>> errors;
    public final Map<ImportId, Path> localSources;

    public Result(Map<ImportId, ROList<Error>> errors, Map<ImportId, Path> localSources) {
      this.errors = errors;
      this.localSources = localSources;
    }
  }
}
