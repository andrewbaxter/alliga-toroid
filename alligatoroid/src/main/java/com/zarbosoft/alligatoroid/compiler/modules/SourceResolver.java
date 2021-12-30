package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;

public interface SourceResolver {
    public Source get(CompileContext context, ModuleId id);
}
