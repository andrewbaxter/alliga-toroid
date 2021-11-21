package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.deserialize.DefaultStateSingle;
import com.zarbosoft.luxem.read.path.LuxemPathBuilder;
import com.zarbosoft.rendaw.common.TSList;

public class BuiltinState extends DefaultStateSingle {
  private String key;

  @Override
  protected void innerEatPrimitiveUntyped(
      TSList<Error> errors, LuxemPathBuilder luxemPath, String value) {
    key = value;
  }

  @Override
  public Object build(TSList<Error> errors) {
    return Cache.builtinMap.get(key);
  }
}
