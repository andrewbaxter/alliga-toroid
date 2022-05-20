package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.Asyncer;
import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Modules {
  private final Asyncer<CacheImportIdRes, Module> modules;
  private final ConcurrentHashMap<ImportId, CacheImportIdRes> cacheIds;
  private final ModuleResolver inner;

  public Modules(ModuleResolver inner) {
    this.inner = inner;
    modules = new Asyncer<>();
    cacheIds = new ConcurrentHashMap<>();
  }

  public CacheImportIdRes getCacheId(ImportId importId) {
    return cacheIds.computeIfAbsent(
        importId,
        new Function<ImportId, CacheImportIdRes>() {
          @Override
          public CacheImportIdRes apply(ImportId importId) {
            return inner.getCacheId(importId);
          }
        });
  }

  public CompletableFuture<Module> getModule(
      CompileContext context, ImportPath fromImportPath, CacheImportIdRes cacheId) {
    return modules.get(
        context.threads,
        cacheId,
        () -> {
          final Source source = context.sources.get(context, cacheId.importId.moduleId);
          cacheId.importId.moduleId.dispatch(
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
          return inner.get(context, fromImportPath, cacheId, source);
        });
  }
}
