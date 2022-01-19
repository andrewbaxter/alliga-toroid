package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.Asyncer;
import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;

import java.util.concurrent.CompletableFuture;

public class Modules {
  private final Asyncer<ImportId, Module> modules;
  private final ModuleResolver inner;

  public Modules(ModuleResolver inner) {
    this.inner = inner;
    modules = new Asyncer<>();
  }

  public CompletableFuture<Module> getModule(
      CompileContext context, ImportPath fromImportPath, ImportId importId) {
    return modules.get(
        context.threads,
        importId,
        () -> {
          final Source source = context.sources.get(context, importId.moduleId);
          importId.moduleId.dispatch(
              new ModuleId.DefaultDispatcher<Object>(null) {
                @Override
                public Object handleLocal(LocalModuleId id) {
                  context.dependents.addHash(id.path, source.hash);
                  if (fromImportPath != null)
                    fromImportPath.spec.moduleId.dispatch(
                        new ModuleId.DefaultDispatcher<Object>(null) {
                          @Override
                          public Object handleLocal(LocalModuleId fromId) {
                            context.dependents.addDependency(fromId.path, id.path);
                            return null;
                          }
                        });
                  return null;
                }
              });
          return inner.get(context, fromImportPath, importId, source);
        });
  }
}
