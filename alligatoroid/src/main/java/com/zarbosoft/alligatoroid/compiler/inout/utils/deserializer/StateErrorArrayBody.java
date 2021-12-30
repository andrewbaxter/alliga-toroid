package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateErrorArrayBody<C, T> extends DefaultStateArrayBody<C, T> {
  public static final StateErrorArrayBody state = new StateErrorArrayBody();

  private StateErrorArrayBody() {}

  @Override
  public BaseStateSingle createElementState(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return StateErrorSingle.state;
  }

  @Override
  public T build(C context, TSList<Error> errors) {
    return null;
  }
}
