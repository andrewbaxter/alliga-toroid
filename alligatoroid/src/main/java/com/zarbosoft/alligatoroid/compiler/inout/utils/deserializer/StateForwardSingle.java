package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateForwardSingle<C, T> extends BaseStateSingle<C, T> {
  private final BaseStateSingle<C, T> inner;

  public StateForwardSingle(BaseStateSingle<C, T> inner) {
    this.inner = inner;
  }

  @Override
  protected BaseStateArrayBody innerArrayBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return inner.innerArrayBegin(context, errors, luxemPath);
  }

  @Override
  protected BaseStateRecordBody innerEatRecordBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return inner.innerEatRecordBegin(context, errors, luxemPath);
  }

  @Override
  protected BaseStateSingle innerEatType(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
    return inner.innerEatType(context, errors, luxemPath, name);
  }

  @Override
  protected void innerEatPrimitiveUntyped(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    inner.innerEatPrimitiveUntyped(context, errors, luxemPath, value);
  }

  @Override
  public T build(C context, TSList<Error> errors) {
    return inner.build(context, errors);
  }
}
