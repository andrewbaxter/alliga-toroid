package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class VariableDataArrayElementValue implements VariableDataValue {
  private final MortarDataType elementType;
  private final MortarCarry carry;

  public VariableDataArrayElementValue(MortarDataType elementType, MortarCarry carry) {
    this.elementType = elementType;
    this.carry = carry;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return elementType.varValueBind(context, mortarVaryCode(context, location));
  }

  @Override
  public MortarDataType mortarType() {
    return elementType;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return new JVMSharedCode().add(carry.drop(context, location));
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location location) {
    return EvaluateResult.pure(this);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return elementType.variableValueAccess(
        context, location, mortarVaryCode(context, location), field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    if (!elementType.assertAssignableFrom(context, location, value))
      return EvaluateResult.error;
    return new EvaluateResult(
        new JVMSharedCode()
            .add(this.carry.half(context))
            .add(((VariableDataValue) value).mortarVaryCode(context, location).half(context))
            .add(JVMSharedCode.setArray(context.sourceLocation(location))),
        null,
        nullValue);
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return MortarCarry.ofDeferredHalf(
        c ->
            new JVMSharedCode()
                .add(carry.half(context))
                .add(JVMSharedCode.accessArray(context.sourceLocation(location))));
  }
}
