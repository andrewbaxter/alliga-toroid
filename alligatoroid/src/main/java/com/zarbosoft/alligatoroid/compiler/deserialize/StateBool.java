package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateBool extends DefaultStatePrimitive {
  private Boolean value = null;

  @Override
  protected void innerEatPrimitiveUntyped(
      TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    if ("true".equals(value)) {
      this.value = true;
    } else if ("false".equals(value)) {
      this.value = false;
    } else {
      errors.add(new Error.DeserializeNotBool(luxemPath.render(), value));
    }
  }

  @Override
  public Object build(TSList<Error> errors) {
    return value;
  }
}
