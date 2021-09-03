package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class StateArrayEnd extends BaseState {
  public static final StateArrayEnd state = new StateArrayEnd();

  private StateArrayEnd() {}

  @Override
  public void eatArrayEnd(TSList<Error> errors, TSList<State> stack, LuxemPath luxemPath) {
    stack.removeLast();
  }
}
