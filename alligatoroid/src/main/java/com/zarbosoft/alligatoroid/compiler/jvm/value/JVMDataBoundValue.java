package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMBinding;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeStoreLoad;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class JVMDataBoundValue implements JVMDataValue {
  private final JVMBinding binding;

  public JVMDataBoundValue(JVMBinding binding) {
    this.binding = binding;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return binding.type.valueAccess(context, location, jvmCode(context, location), field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    if (!binding.type.assertAssignableFrom(context, location, value)) return EvaluateResult.error;
    return new EvaluateResult(
        new JVMSharedCode()
            .add(((JVMDataValue) value).jvmCode(context, location).code(context))
            .add(new JVMSharedCodeStoreLoad(binding.type.storeOpcode(), binding.key)),
        null,
        nullValue);
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return binding.type.valueBind(context, jvmCode(context, location));
  }

  @Override
  public JVMType jvmType() {
    return binding.type;
  }

  @Override
  public JVMProtocode jvmCode(EvaluationContext context, Location location) {
    return JVMProtocode.ofDeferred(
        c -> new JVMSharedCodeStoreLoad(binding.type.loadOpcode(), binding.key));
  }
}
