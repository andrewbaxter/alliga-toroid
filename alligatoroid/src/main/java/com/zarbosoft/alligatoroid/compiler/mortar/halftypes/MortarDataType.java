package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.CallNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeStack;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataConstValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataVariableValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public interface MortarDataType {

  default EvaluateResult type_variableValueAccess(
      EvaluationContext context, Location location, MortarDeferredCode base, Value field) {
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  default MortarDataVariableValue type_forkBinding(MortarDataBinding binding) {
    return new MortarDataVariableValue(
        binding.type.type_fork(),
        new MortarDeferredCodeBinding(
            type_loadBytecode(binding.key), type_storeBytecode(binding.key)));
  }

  default MortarDataType type_fork() {
    return this;
  }

  default ROPair<TargetCode, Binding> type_varValueBind(
          EvaluationContext context, JavaBytecode code) {
    JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
    return new ROPair<>(
        new MortarTargetCode(
            new JavaBytecodeSequence()
                .add(((MortarTargetCode) code).e)
                .add(type_storeBytecode(key))),
        new MortarDataBinding(key, this));
  }

  JavaDataDescriptor type_jvmDesc();

  JavaBytecode type_returnBytecode();

  JavaBytecode type_storeBytecode(JavaBytecodeBindingKey key);

  JavaBytecode type_loadBytecode(JavaBytecodeBindingKey key);

  JavaBytecode type_arrayStoreBytecode();

  JavaBytecode type_arrayLoadBytecode();

  default MortarDataVariableValue type_stackAsValue(JavaBytecode code) {
    return new MortarDataVariableValue(this, new MortarDeferredCodeStack(code));
  }

  default Value type_constAsValue(Object value) {
    return MortarDataConstValue.create(this, value);
  }

  default EvaluateResult type_constValueAccess(
      EvaluationContext context, Location location, Object value, Value field) {
    // Duplicated in Value
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  default EvaluateResult type_constCall(
      EvaluationContext context, Location location, Object inner, Value argument) {
    context.errors.add(new CallNotSupported(location));
    return EvaluateResult.error;
  }

  default ROList<String> type_traceFields(
      EvaluationContext context, Location location, Object inner) {
    return ROList.empty;
  }

  EvaluateResult type_valueVary(EvaluationContext context, Location id, Object data);

  /**
   * Only called if canCastTo is true
   * @param prototype
   * @param code
   * @return
   */
  JavaBytecode type_castTo(MortarDataPrototype prototype, MortarDeferredCode code);

  boolean type_canCastTo(MortarDataPrototype prototype);
}
