package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedJVMName;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class VariableDataFieldValue implements VariableDataValue {
  private final MortarDataFieldType type;
  private final MortarCarry carry;

  public VariableDataFieldValue(MortarDataFieldType type, MortarCarry carry) {
    this.type = type;
    this.carry = carry;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.type.varValueBind(context, mortarVaryCode(context, location));
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return MortarCarry.ofHalf(
        new JVMSharedCode()
            .add(carry.half(context))
            .add(
                JVMSharedCode.accessField(
                    context.sourceLocation(location),
                    JVMSharedJVMName.fromClass(type.field.getDeclaringClass()),
                    type.field.getName(),
                    type.type.jvmDesc())),
        carry.drop(context, location));
  }

  @Override
  public MortarDataType mortarType() {
    return type.type;
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
    return type.type.variableValueAccess(context, location, mortarVaryCode(context, location), field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    if (!type.type.assertAssignableFrom(context, location, value))
      return EvaluateResult.error;
    final MortarCarry carry = ((VariableDataValue) value).mortarVaryCode(context, location);
    return new EvaluateResult(
        new JVMSharedCode()
            .add(carry.half(context))
            .add(
                JVMSharedCode.accessField(
                    context.sourceLocation(location),
                    JVMSharedJVMName.fromClass(type.field.getDeclaringClass()),
                    type.field.getName(),
                    type.type.jvmDesc())),
        null,
        nullValue);
  }
}
