package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.rendaw.common.TSList;

public class StateLong<C> extends DefaultStateLong<C, Long> {
  @Override
  public Long build(C context, TSList<Error> errors) {
    return value;
  }
}
