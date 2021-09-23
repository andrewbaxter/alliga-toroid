package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.TSList;

public class StateBool extends DefaultStatePrimitive{
  private Boolean value = null;

  @Override
  protected void innerEatPrimitiveUntyped(TSList<Error> errors, LuxemPath luxemPath, String value) {
    if ("true".equals(value)) {
      this.value = true;
    } else if ("false".equals(value)) {
      this.value = false;
    } else {
      errors.add(Error.deserializeNotBool(luxemPath, value));
    }
  }

  @Override
  public Object build(TSList<Error> errors) {
    return value;
  }
}
