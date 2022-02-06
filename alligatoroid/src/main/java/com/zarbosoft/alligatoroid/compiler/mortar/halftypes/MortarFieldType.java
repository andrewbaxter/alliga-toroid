package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;

public interface MortarFieldType {
  EvaluateResult constFieldAsValue(EvaluationContext context, Location location, Object base);

  EvaluateResult variableFieldAsValue(
      EvaluationContext context,
      Location location,
      MortarCarry targetCarry,
      MortarAutoObjectType baseType);
}
