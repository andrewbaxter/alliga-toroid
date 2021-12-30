package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.rendaw.common.TSList;

public class StateBool<C> extends DefaultStateBool<C, Boolean> {
  @Override
  public Boolean build(C context, TSList<Error> errors) {
    return value;
  }
}
