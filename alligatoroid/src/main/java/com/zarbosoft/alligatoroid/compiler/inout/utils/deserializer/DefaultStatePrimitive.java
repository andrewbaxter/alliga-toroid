package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public abstract class DefaultStatePrimitive<C,T> extends DefaultStateSingle<C,T> {
  protected String out;

  @Override
  protected void innerEatPrimitiveUntyped(C context,
      TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    out = value;
  }
}
