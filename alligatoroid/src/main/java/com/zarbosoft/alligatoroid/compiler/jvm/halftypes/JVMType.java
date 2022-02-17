package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.BindingKey;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMBinding;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMDataBoundValue;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMDataStackValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.SetNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext.correspondJvmType;
import static org.objectweb.asm.Opcodes.POP;

public interface JVMType extends Exportable {
  default EvaluateResult valueAccess(
      EvaluationContext context, Location location, JVMProtocode carry, Value field) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  default Error checkAssignableFrom(Location location, JVMType type) {
    return new SetNotSupported(location);
  }
  default boolean assertAssignableFrom(EvaluationContext context, Location location, Value value) {
    final Error error = checkAssignableFrom(location, correspondJvmType(value));
    if (error != null) {
      context.moduleContext.errors.add(error);
      return false;
    }
    return true;
  }

  default ROPair<TargetCode, Binding> valueBind(EvaluationContext context, JVMProtocode code) {
    BindingKey key = new BindingKey();
    return new ROPair<>(
        new JVMSharedCode().add(code.code(context)).addVarInsn(storeOpcode(), key),
        new JVMBinding(key, this));
  }

  default Value stackAsValue(JVMSharedCodeElement code) {
    return new JVMDataStackValue(JVMProtocode.of(code, JVMSharedCode.inst(POP)), this);
  }

  int storeOpcode();

  int loadOpcode();

  JVMSharedDataDescriptor jvmDesc();

  default Value boundAsValue(JVMBinding binding) {
    return new JVMDataBoundValue(binding);
  }
}
