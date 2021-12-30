package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

public abstract class BaseStateSingle<C, T> implements State<C, T> {
  @Override
  public final void eatArrayBegin(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    popSelf(stack);
    stack.add(innerArrayBegin(context, errors, luxemPath));
  }

  protected abstract BaseStateArrayBody innerArrayBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath);

  @Override
  public final void eatArrayEnd(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    throw new Assertion();
  }

  @Override
  public final void eatRecordBegin(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    popSelf(stack);
    stack.add(innerEatRecordBegin(context, errors, luxemPath));
  }

  protected abstract BaseStateRecordBody innerEatRecordBegin(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath);

  @Override
  public final void eatRecordEnd(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    throw new Assertion();
  }

  @Override
  public final void eatType(
      C context,
      TSList<Error> errors,
      TSList<State> stack,
      LuxemPathBuilder luxemPath,
      String name) {
    popSelf(stack);
    stack.add(innerEatType(context, errors, luxemPath, name));
  }

  protected abstract BaseStateSingle innerEatType(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String name);

  @Override
  public final void eatPrimitive(
      C context,
      TSList<Error> errors,
      TSList<State> stack,
      LuxemPathBuilder luxemPath,
      String value) {
    popSelf(stack);
    innerEatPrimitiveUntyped(context, errors, luxemPath, value);
  }

  protected abstract void innerEatPrimitiveUntyped(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String value);
}
