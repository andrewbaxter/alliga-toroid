package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public class JVMDataStackValue implements JVMDataValue {
  public final JVMProtocode carry;
  public final JVMType type;

  public JVMDataStackValue(JVMProtocode carry, JVMType type) {
    this.carry = carry;
    this.type = type;
  }

  @Override
  public JVMProtocode jvmCode(EvaluationContext context, Location location) {
    return carry;
  }

  @Override
  public JVMType jvmType() {
    return type;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.valueBind(context, carry);
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
    return type.valueAccess(context, location, carry, field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    context.moduleContext.errors.add(new CantSetStackValue(location));
    return EvaluateResult.error;
  }
}
