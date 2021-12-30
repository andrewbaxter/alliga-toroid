package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeNotInteger;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public abstract class DefaultStateInt<C,T> extends DefaultStateSingle<C,T> {
  protected Integer value;

  @Override
  protected void innerEatPrimitiveUntyped(C context,
          TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    try {
      this.value = Integer.parseInt(value);
    } catch (NumberFormatException e) {
      errors.add(new DeserializeNotInteger(luxemPath.render(), value));
    }
  }
}
