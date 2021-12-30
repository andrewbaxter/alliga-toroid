package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.rendaw.common.Assertion;

public class NullValue implements MortarProtocode, SimpleValue {
  public static final NullValue value = new NullValue();

  private NullValue() {}

  @Override
  public MortarCode lower() {
    throw new Assertion();
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }
}
