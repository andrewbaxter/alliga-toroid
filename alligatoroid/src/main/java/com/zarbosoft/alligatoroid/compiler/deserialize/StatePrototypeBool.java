package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class StatePrototypeBool implements StatePrototype {
  public static final StatePrototypeBool instance = new StatePrototypeBool();

  private StatePrototypeBool() {}

  @Override
  public BaseStateSingle create(TSList<Error> errors, LuxemPath luxemPath) {
    return new StateBool();
  }
}
