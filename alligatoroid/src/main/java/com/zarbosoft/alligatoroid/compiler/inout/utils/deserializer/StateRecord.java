package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateRecord<C, T> extends DefaultStateSingle<C, T> {
  private final BaseStateRecordBody<C, T> inner;

  public StateRecord(BaseStateRecordBody<C, T> inner) {
    this.inner = inner;
  }

  @Override
  protected BaseStateRecordBody innerEatRecordBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return inner;
  }

  @Override
  public T build(C context, TSList<Error> errors) {
    return inner.build(context, errors);
  }
}
