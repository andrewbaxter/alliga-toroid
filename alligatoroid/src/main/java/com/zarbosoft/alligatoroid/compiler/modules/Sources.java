package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.Asyncer;
import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;

public class Sources {
  private final SourceResolver inner;
  private final Asyncer<Object, Source> downloads;

  public Sources(SourceResolver inner) {
    this.inner = inner;
    downloads = new Asyncer<>();
  }

  public Source get(CompileContext context, ImportId importId) {
    return downloads.getSync(
        context.threads,
        importId.moduleId,
        () -> {
          final Source out = inner.get(context, importId.moduleId);
          context.localSources.put(importId, out.path);
          return out;
        });
  }
}
