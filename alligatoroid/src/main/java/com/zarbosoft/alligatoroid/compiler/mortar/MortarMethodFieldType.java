package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstMethodFieldValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableMethodFieldValue;

public class MortarMethodFieldType implements MortarFieldType {
  public final Meta.FuncInfo funcInfo;

  public MortarMethodFieldType(Meta.FuncInfo funcInfo) {
    this.funcInfo = funcInfo;
  }

  @Override
  public EvaluateResult constFieldAsValue(EvaluationContext context, Location location, Object base) {
  return EvaluateResult.pure(new ConstMethodFieldValue(this, base));
  }

  public EvaluateResult variableFieldAsValue(
      EvaluationContext context,
      Location location,
      MortarCarry targetCarry,
      MortarAutoObjectType baseType) {
    return EvaluateResult.pure(new VariableMethodFieldValue(baseType, targetCarry, this));
  }
}
