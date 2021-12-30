package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.rendaw.common.TSList;

public class StateString<C> extends DefaultStatePrimitive<C, String> {
  @Override
  public String build(C context, TSList<Error> errors) {
    return out;
  }
}
