package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataStackValue.nullValue;

public class VariableBoundDataValue implements VariableDataValue {
  private final MortarDataBinding binding;

  public VariableBoundDataValue(MortarDataBinding binding) {
    this.binding = binding;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return binding.type.varValueBind(context, mortarVaryCode(context, location));
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return MortarCarry.ofDeferredHalf(
        c -> new JVMSharedCode().addVarInsn(binding.type.loadOpcode(), binding.key));
  }

  @Override
  public MortarDataType mortarType() {
    return binding.type;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location location) {
    return EvaluateResult.pure(this);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return binding.type.variableValueAccess(
        context, location, mortarVaryCode(context, location), field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    if (!binding.type.assertAssignableFrom(context, location, value.mortarType()))
      return EvaluateResult.error;
    final MortarCarry carry = ((VariableDataStackValue) value).carry;
    return new EvaluateResult(
        new JVMSharedCode()
            .add(carry.half(context))
            .addVarInsn(binding.type.storeOpcode(), binding.key),
        null,
        nullValue);
  }
}
