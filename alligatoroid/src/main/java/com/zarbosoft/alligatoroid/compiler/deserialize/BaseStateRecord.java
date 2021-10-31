package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

public abstract class BaseStateRecord implements State {
  private State key;

  public abstract BaseStateSingle createKeyState(TSList<Error> errors, LuxemPathBuilder luxemPath);

  public abstract BaseStateSingle createValueState(
          TSList<Error> errors, LuxemPathBuilder luxemPath, Object key);

  @Override
  public final void eatType(
          TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath, String name) {
    push(errors, stack, luxemPath);
    stack.last().eatType(errors, stack, luxemPath, name);
  }

  @Override
  public final void eatPrimitive(
          TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath, String value) {
    push(errors, stack, luxemPath);
    stack.last().eatPrimitive(errors, stack, luxemPath, value);
  }

  @Override
  public final void eatArrayBegin(TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    push(errors, stack, luxemPath);
    stack.last().eatArrayBegin(errors, stack, luxemPath);
  }

  private void push(TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    if (key == null) {
      key = createKeyState(errors, luxemPath);
      stack.add(key);
    } else {
      Object key = this.key.build(errors);
      this.key = null;
      BaseStateSingle state = createValueState(errors, luxemPath, key);
      stack.add(state);
    }
  }

  @Override
  public final void eatRecordEnd(TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    popSelf(stack);
  }

  @Override
  public void eatArrayEnd(TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    throw new Assertion();
  }

  @Override
  public final void eatRecordBegin(TSList<Error> errors, TSList<State> stack, LuxemPathBuilder luxemPath) {
    push(errors, stack, luxemPath);
    stack.last().eatRecordBegin(errors, stack, luxemPath);
  }
}
