package com.zarbosoft.alligatoroid.compiler.deserialize;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class StateInt extends DefaultStatePrimitive {
  private Integer value = null;

  @Override
  protected void innerEatPrimitiveUntyped(TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    try {
      this.value = Integer.parseInt(value);
    } catch (NumberFormatException e) {
        errors.add(new Error.DeserializeNotInteger(luxemPath.render(), value));
    }
  }

  @Override
  public Object build(TSList<Error> errors) {
    return value;
  }
}
