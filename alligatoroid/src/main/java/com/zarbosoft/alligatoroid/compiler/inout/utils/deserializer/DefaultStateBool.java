package com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer;

import com.zarbosoft.alligatoroid.compiler.model.error.DeserializeNotBool;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public abstract class DefaultStateBool<C, T> extends DefaultStateSingle<C, T> {
  public static final String BOOL_TRUE = "true";
  public static final String BOOL_FALSE = "false";
  protected Boolean value = null;

  @Override
  protected void innerEatPrimitiveUntyped(
      C context, TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    if (BOOL_TRUE.equals(value)) {
      this.value = true;
    } else if (BOOL_FALSE.equals(value)) {
      this.value = false;
    } else {
      errors.add(new DeserializeNotBool(luxemPath.render(), value));
    }
  }
}
