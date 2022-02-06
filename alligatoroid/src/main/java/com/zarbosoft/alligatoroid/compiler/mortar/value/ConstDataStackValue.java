package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;

public class ConstDataStackValue extends ConstDataValue {
  public static final ConstDataStackValue nullValue =
      new ConstDataStackValue(MortarNullType.type, null);
  private final MortarDataType type;
  public Object value;

  public ConstDataStackValue(MortarDataType type, Object value) {
    this.type = type;
    this.value = value;
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return MortarCarry.ofDeferredHalf(c -> type.constValueVary(context, value));
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    context.moduleContext.errors.add(new CantSetStackValue(location));
    return EvaluateResult.error;
  }

  @Override
  public Object getInner() {
    return value;
  }

  @Override
  public MortarDataType mortarType() {
    return type;
  }
}