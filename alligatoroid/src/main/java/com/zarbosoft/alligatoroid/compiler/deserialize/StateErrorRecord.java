package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateErrorRecord extends BaseStateRecord {
  @Override
  public BaseStateSingle createKeyState(TSList<Error> errors, LuxemPathBuilder luxemPath) {
    return StateErrorSingle.state;
  }

  @Override
  public BaseStateSingle createValueState(
      TSList<Error> errors, LuxemPathBuilder luxemPath, Object key) {
    return StateErrorSingle.state;
  }

  @Override
  public Object build(TSList<Error> errors) {
    return null;
  }
}
