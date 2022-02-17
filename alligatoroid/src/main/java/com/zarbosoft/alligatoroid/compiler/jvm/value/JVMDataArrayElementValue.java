package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class JVMDataArrayElementValue implements JVMDataValue {
  private final JVMType elementType;
  private final JVMProtocode carry;

  public JVMDataArrayElementValue(JVMType elementType, JVMProtocode carry) {
    this.elementType = elementType;
    this.carry = carry;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return carry.drop(context, location);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return elementType.valueAccess(context, location, jvmCode(context, location), field);
  }

  @Override
  public JVMType jvmType() {
    return elementType;
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    if (!elementType.assertAssignableFrom(context, location, value)) return EvaluateResult.error;
    return new EvaluateResult(
        new JVMSharedCode()
            .add(this.carry.code(context))
            .add(((JVMDataValue) value).jvmCode(context, location).code(context))
            .add(JVMSharedCode.setArray(context.sourceLocation(location))),
        null,
        nullValue);
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return elementType.valueBind(context, jvmCode(context, location));
  }

  @Override
  public JVMProtocode jvmCode(EvaluationContext context, Location location) {
    return JVMProtocode.ofDeferred(
        c ->
            new JVMSharedCode()
                .add(carry.code(context))
                .add(JVMSharedCode.accessArray(context.sourceLocation(location))));
  }
}
