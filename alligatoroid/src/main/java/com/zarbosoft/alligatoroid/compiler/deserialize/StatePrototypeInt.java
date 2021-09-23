package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class StatePrototypeInt implements StatePrototype {
  public static final StatePrototypeInt instance = new StatePrototypeInt();

  private StatePrototypeInt() {}

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPath luxemPath) {
    return new StateInt();
  }
}
