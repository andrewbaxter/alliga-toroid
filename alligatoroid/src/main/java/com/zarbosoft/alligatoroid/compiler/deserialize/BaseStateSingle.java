package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

public abstract class BaseStateSingle implements State {
  @Override
  public final void eatArrayBegin(
      TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    popSelf(stack);
    stack.add(innerArrayBegin(errors, luxemPath));
  }

  protected abstract BaseStateArray innerArrayBegin(
      TSList<Error> errors, LuxemPathBuilder luxemPath);

  @Override
  public final void eatArrayEnd(
      TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    throw new Assertion();
  }

  @Override
  public final void eatRecordBegin(
      TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    popSelf(stack);
    stack.add(innerEatRecordBegin(errors, luxemPath));
  }

  protected abstract BaseStateRecord innerEatRecordBegin(
      TSList<Error> errors, LuxemPathBuilder luxemPath);

  @Override
  public final void eatRecordEnd(
      TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    throw new Assertion();
  }

  @Override
  public final void eatType(
      TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath, String name) {
    popSelf(stack);
    stack.add(innerEatType(errors, luxemPath, name));
  }

  protected abstract BaseStateSingle innerEatType(
      TSList<Error> errors, LuxemPathBuilder luxemPath, String name);

  @Override
  public final void eatPrimitive(
      TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath, String value) {
    popSelf(stack);
    innerEatPrimitiveUntyped(errors, luxemPath, value);
  }

  protected abstract void innerEatPrimitiveUntyped(
      TSList<Error> errors, LuxemPathBuilder luxemPath, String value);
}
