package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.TSList;

public class StateArrayBegin implements State {
  public static final StateArrayBegin state = new StateArrayBegin();

  private StateArrayBegin() {}

  @Override
  public void eatArrayBegin(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    stack.removeLast();
  }

  @Override
  public void eatArrayEnd(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    throw new Assertion();
  }

  @Override
  public void eatRecordBegin(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    errors.add(Error.deserializeNotRecord(luxemPath));
    stack.removeLast();
  }

  @Override
  public void eatRecordEnd(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    throw new Assertion();
  }

  @Override
  public void eatKey(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String name) {
    throw new Assertion();
  }

  @Override
  public void eatType(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String name) {
    stack.removeLast();
    stack.add(StateErrorSingle.state);
  }

  @Override
  public void eatPrimitive(
      TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath, String value) {
    errors.add(Error.deserializeNotPrimitive(luxemPath));
    stack.removeLast();
  }

  @Override
  public Object build(TSList<Error> errors) {
    throw new Assertion();
  }
}
