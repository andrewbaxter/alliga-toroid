package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateErrorRecordBody<C, T> extends BaseStateRecordBody<C, T> {
  @Override
  public BaseStateSingle createKeyState(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return StateErrorSingle.state;
  }

  @Override
  public BaseStateSingle createValueState(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, Object key) {
    return StateErrorSingle.state;
  }

  @Override
  public T build(C context, TSList<Error> errors) {
    return null;
  }
}
