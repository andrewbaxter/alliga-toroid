package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarProtofield;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectFieldtype;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarMethodValue;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarMethodFieldtype implements MortarObjectFieldtype, MortarProtofield {
  public final StaticAutogen.FuncInfo funcInfo;

  public MortarMethodFieldtype(StaticAutogen.FuncInfo funcInfo) {
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
    return EvaluateResult.pure(new MortarMethodValue(funcInfo, targetCarry));
  }
}
