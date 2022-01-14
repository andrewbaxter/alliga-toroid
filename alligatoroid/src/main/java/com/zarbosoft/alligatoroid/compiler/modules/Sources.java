package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.Asyncer;
import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;

public class Sources {
  private final SourceResolver inner;
  private final Asyncer<Object, Source> downloads;

  public Sources(SourceResolver inner) {
    this.inner = inner;
    downloads = new Asyncer<>();
  }

  public Source get(CompileContext context, ModuleId moduleId) {
    return downloads.getSync(
        context.threads,
        moduleId,
        () -> {
          final Source out = inner.get(context, moduleId);
          context.localSources.put(moduleId, out.path);
          return out;
        });
  }
}
