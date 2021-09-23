package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

public abstract class DefaultStateArray implements BaseStateArray {
  public abstract BaseStateSingle createElementState(TSList<Error> errors, LuxemPath luxemPath);

  @Override
  public final void eatType(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String name) {
    push(errors, stack, luxemPath);
    stack.last().eatType(errors, stack, luxemPath, name);
  }

  @Override
  public final void eatPrimitive(
      TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String value) {
    push(errors, stack, luxemPath);
    stack.last().eatPrimitive(errors, stack, luxemPath, value);
  }

  @Override
  public final void eatArrayBegin(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    push(errors, stack, luxemPath);
    stack.last().eatArrayBegin(errors, stack, luxemPath);
  }

  private void push(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    BaseStateSingle state = createElementState(errors, luxemPath);
    stack.add(state);
  }

  @Override
  public final void eatArrayEnd(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    popSelf(stack);
  }

  @Override
  public final void eatRecordEnd(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    throw new Assertion();
  }

  @Override
  public final void eatRecordBegin(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    push(errors, stack, luxemPath);
    stack.last().eatRecordBegin(errors, stack, luxemPath);
  }
}
