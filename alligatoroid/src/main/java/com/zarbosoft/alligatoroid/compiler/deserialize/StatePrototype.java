package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public interface StatePrototype {
  public BaseStateSingle create(TSList<Error> errors, LuxemPath luxemPath);
}
