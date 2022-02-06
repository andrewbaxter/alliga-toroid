package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.ROPair;

public class VariableDataStackValue implements VariableDataValue {
  public final MortarCarry carry;
  public final MortarDataType type;

  public VariableDataStackValue(MortarCarry carry, MortarDataType type) {
    this.carry = carry;
    this.type = type;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.varValueBind(context, carry);
  }

  @Override
  public MortarDataType mortarType() {
    return type;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return carry.drop(context, location);
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location location) {
    return EvaluateResult.pure(this);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return type.variableValueAccess(context, location, carry, field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    context.moduleContext.errors.add(new CantSetStackValue(location));
    return EvaluateResult.error;
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return carry;
  }
}
