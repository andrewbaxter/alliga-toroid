package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ImportPath;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;

public interface ModuleResolver {
  public abstract Module get(
          CompileContext context, ImportPath fromImportPath, ImportId importId, Source source);
}
