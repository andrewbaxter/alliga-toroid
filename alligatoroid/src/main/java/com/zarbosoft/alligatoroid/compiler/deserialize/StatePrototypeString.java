package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class StatePrototypeString implements StatePrototype {
  public static final StatePrototypeString instance = new StatePrototypeString();

  private StatePrototypeString() {}

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPath luxemPath) {
    return new StateString();
  }
}
