package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

public abstract class BaseStateRecordBody<C, T> implements State<C, T> {
  private State key;

  public abstract BaseStateSingle createKeyState(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath);

  public abstract BaseStateSingle createValueState(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, Object key);

  @Override
  public final void eatType(
      C context,
      TSList<Error> errors,
      TSList<State> stack,
      LuxemPathBuilder luxemPath,
      String name) {
    push(context, errors, stack, luxemPath);
    stack.last().eatType(context, errors, stack, luxemPath, name);
  }

  @Override
  public final void eatPrimitive(
      C context,
      TSList<Error> errors,
      TSList<State> stack,
      LuxemPathBuilder luxemPath,
      String value) {
    push(context, errors, stack, luxemPath);
    stack.last().eatPrimitive(context, errors, stack, luxemPath, value);
  }

  @Override
  public final void eatArrayBegin(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    push(context, errors, stack, luxemPath);
    stack.last().eatArrayBegin(context, errors, stack, luxemPath);
  }

  private void push(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    if (key == null) {
      key = createKeyState(context, errors, luxemPath);
      stack.add(key);
    } else {
      Object key = this.key.build(context, errors);
      this.key = null;
      BaseStateSingle state = createValueState(context, errors, luxemPath, key);
      stack.add(state);
    }
  }

  @Override
  public final void eatRecordEnd(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    popSelf(stack);
  }

  @Override
  public void eatArrayEnd(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    throw new Assertion();
  }

  @Override
  public final void eatRecordBegin(
      C context, TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    push(context, errors, stack, luxemPath);
    stack.last().eatRecordBegin(context, errors, stack, luxemPath);
  }
}
