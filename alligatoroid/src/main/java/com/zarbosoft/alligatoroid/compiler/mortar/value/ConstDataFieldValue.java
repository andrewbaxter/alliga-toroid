package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.ConstExportType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class ConstDataFieldValue extends ConstDataValue {
  private final Object base;
  private final MortarDataFieldType type;

  public ConstDataFieldValue(MortarDataFieldType type, Object base) {
    this.type = type;
    this.base = base;
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return MortarCarry.ofDeferredHalf(c -> type.type.constValueVary(context, getInner()));
  }

  @Override
  public Object getInner() {
    return uncheck(() -> type.field.get(base));
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    return setHelper(
        this, context, location, value, v -> uncheck(() -> type.field.set(base, value)));
  }

  @Override
  public MortarDataType mortarType() {
    return type.type;
  }
}
