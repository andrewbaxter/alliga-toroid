package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.rendaw.common.Assertion;

public class ConstDataBuiltinSingletonValue extends ConstDataValue
    implements SingletonBuiltinExportable {
  public static final ConstDataBuiltinSingletonValue nullValue =
      new ConstDataBuiltinSingletonValue(MortarNullType.type, null);
  public MortarDataType type;
  public Object value;

  public ConstDataBuiltinSingletonValue(MortarDataType type, Object value) {
    if (type == null) throw new Assertion();
    this.type = type;
    this.value = value;
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return MortarCarry.ofDeferredHalf(c -> type.constValueVary(context, value));
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
