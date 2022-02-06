package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataFieldValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataFieldValue;

import java.lang.reflect.Field;

public class MortarDataFieldType implements MortarFieldType {
  public final Field field;
  public final MortarDataType type;

  public MortarDataFieldType(Field field, MortarDataType type) {
    this.field = field;
    this.type = type;
  }

  @Override
  public EvaluateResult constFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(new ConstDataFieldValue(this, base));
  }

  @Override
  public EvaluateResult variableFieldAsValue(
      EvaluationContext context,
      Location location,
      MortarCarry targetCarry,
      MortarAutoObjectType baseType) {
    return EvaluateResult.pure(new VariableDataFieldValue(this, targetCarry));
  }
}
