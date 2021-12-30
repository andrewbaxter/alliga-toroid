package com.zarbosoft.alligatoroid.compiler.modules;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.rendaw.common.ROList;

public interface ErrorResult extends ModuleResult{
    public ROList<Error> errors();
}
