package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarFieldProtoType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MethodValue;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarMethodFieldType implements MortarObjectFieldType, MortarFieldProtoType {
  public final Meta.FuncInfo funcInfo;

  public MortarMethodFieldType(Meta.FuncInfo funcInfo) {
    this.funcInfo = funcInfo;
  }

  @Override
  public EvaluateResult constObjectFieldAsValue(EvaluationContext context, Location location, Object base) {
  throw new Assertion();
  }

  public EvaluateResult variableFieldAsValue(
      EvaluationContext context,
      Location location,
      MortarCarry targetCarry,
      MortarAutoObjectType baseType) {
    return EvaluateResult.pure(new MethodValue(funcInfo, targetCarry));
  }
}
