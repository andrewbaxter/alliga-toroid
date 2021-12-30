package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateErrorSingle<C, T> extends BaseStateSingle<C, T> {
  public static final StateErrorSingle state = new StateErrorSingle();

  private StateErrorSingle() {}

  @Override
  public T build(C context, TSList<Error> errors) {
    return null;
  }

  @Override
  protected DefaultStateArrayBody innerArrayBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return StateErrorArrayBody.state;
  }

  @Override
  protected BaseStateRecordBody innerEatRecordBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return new StateErrorRecordBody();
  }

  @Override
  protected BaseStateSingle innerEatType(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String name) {
    return this;
  }

  @Override
  protected void innerEatPrimitiveUntyped(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {}
}
