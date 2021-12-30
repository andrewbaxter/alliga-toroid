package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateArray<C, T> extends DefaultStateSingle<C, T> {
  private final BaseStateArrayBody<C, T> inner;

  public StateArray(BaseStateArrayBody<C, T> inner) {
    this.inner = inner;
  }

  @Override
  protected BaseStateArrayBody innerArrayBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return inner;
  }

  @Override
  public T build(C context, TSList<Error> errors) {
    return inner.build(context, errors);
  }
}
