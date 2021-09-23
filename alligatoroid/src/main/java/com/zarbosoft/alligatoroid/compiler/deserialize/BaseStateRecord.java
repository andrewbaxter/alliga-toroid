package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseStateRecord implements State {
  private State key;

  public abstract BaseStateSingle createKeyState(TSList<Error> errors, LuxemPath luxemPath);

  public abstract BaseStateSingle createValueState(
      TSList<Error> errors, LuxemPath luxemPath, Object key);

  @Override
  public final void eatType(
      TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String name) {
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
  public final void eatRecordEnd(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    popSelf(stack);
  }

  @Override
  public void eatArrayEnd(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    throw new Assertion();
  }

  @Override
  public final void eatRecordBegin(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    push(errors, stack, luxemPath);
    stack.last().eatRecordBegin(errors, stack, luxemPath);
  }
}
