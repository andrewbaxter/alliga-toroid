package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class DefaultStatePrimitive extends DefaultStateSingle {
  private String out;

  @Override
  protected void innerEatPrimitiveUntyped(TSList<Error> errors, LuxemPath luxemPath, String value) {
    out = value;
  }

  @Override
  public Object build(TSList<Error> errors) {
    return out;
  }
}
