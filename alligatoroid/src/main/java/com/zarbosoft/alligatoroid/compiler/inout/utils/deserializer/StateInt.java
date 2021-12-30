package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.rendaw.common.TSList;

public class StateInt<C> extends DefaultStateInt<C, Integer> {
  @Override
  public Integer build(C context, TSList<Error> errors) {
    return value;
  }
}
