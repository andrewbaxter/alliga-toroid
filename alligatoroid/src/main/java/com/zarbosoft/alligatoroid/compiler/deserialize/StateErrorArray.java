package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateErrorArray extends DefaultStateArray {
  public static final StateErrorArray state = new StateErrorArray();

  private StateErrorArray() {}

  @Override
  public BaseStateSingle createElementState(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return StateErrorSingle.state;
  }

  @Override
  public Object build(TSList<Error> errors) {
    return null;
  }
}
