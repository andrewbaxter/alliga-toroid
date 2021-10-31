package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public interface StatePrototype {
  public BaseStateSingle create(TSList<Error> errors, LuxemPathBuilder luxemPath);
}
