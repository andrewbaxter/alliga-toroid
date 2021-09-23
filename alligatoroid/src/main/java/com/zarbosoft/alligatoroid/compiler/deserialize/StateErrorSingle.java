package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class StateErrorSingle extends BaseStateSingle {
  public static final StateErrorSingle state = new StateErrorSingle();

  private StateErrorSingle() {}

  @Override
  public Object build(TSList<Error> errors) {
    return null;
  }

  @Override
  protected DefaultStateArray innerArrayBegin(TSList<Error> errors, LuxemPath luxemPath) {
    return StateErrorArray.state;
  }

  @Override
  protected BaseStateRecord innerEatRecordBegin(TSList<Error> errors, LuxemPath luxemPath) {
    return new StateErrorRecord();
  }

  @Override
  protected BaseStateSingle innerEatType(TSList<Error> errors, LuxemPath luxemPath, String name) {
    return this;
  }

  @Override
  protected void innerEatPrimitiveUntyped(
      TSList<Error> errors, LuxemPath luxemPath, String value) {}
}
