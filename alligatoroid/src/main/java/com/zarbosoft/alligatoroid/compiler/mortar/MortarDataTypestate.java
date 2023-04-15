package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatch;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatchKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatchStart;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.CallNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableDeferred;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public interface MortarDataTypestate {

  default EvaluateResult typestate_varAccess(
      EvaluationContext context, Location location, Value field, MortarDeferredCode baseCode) {
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  default ROPair<JavaBytecode, Binding> typestate_varBind(EvaluationContext context) {
    JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
    final JavaBytecodeCatchKey javaBytecodeCatchKey = new JavaBytecodeCatchKey();
    return new ROPair<>(
        new JavaBytecodeSequence()
            .add(typestate_storeBytecode(key))
            .add(new JavaBytecodeCatchStart(javaBytecodeCatchKey)),
        new MortarDataVarBinding(key, typestate_newBinding(), javaBytecodeCatchKey));
  }

  JavaBytecode typestate_storeBytecode(JavaBytecodeBindingKey key);

  MortarDataBindstate typestate_newBinding();

  default ROPair<TargetCode, Binding> typestate_constBind(
      EvaluationContext context, Location location, Object data) {
    JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
    final JavaBytecodeCatchKey javaBytecodeCatchKey = new JavaBytecodeCatchKey();
    return new ROPair<>(null, new ConstBinding(typestate_newBinding(), data));
  }

  default Value typestate_constAsValue(Object value) {
    return MortarDataValueConst.create(this, value);
  }

  default EvaluateResult typestate_constValueAccess(
      EvaluationContext context, Location location, Object value, Value field) {
    // Duplicated in Value
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  default EvaluateResult typestate_constCall(
      EvaluationContext context, Location location, Object inner, Value argument) {
    context.errors.add(new CallNotSupported(location));
    return EvaluateResult.error;
  }

  default ROList<String> typestate_traceFields(
      EvaluationContext context, Location location, Object inner) {
    return ROList.empty;
  }

  EvaluateResult typestate_constVary(EvaluationContext context, Location id, Object data);

  /**
   * Only called if canCastTo is true
   *
   * @param context
   * @param location
   * @param prototype
   * @return
   */
  EvaluateResult typestate_varCastTo(
      EvaluationContext context, Location location, MortarDataType prototype);

  EvaluateResult typestate_constCastTo(
      EvaluationContext context, Location location, MortarDataType type, Object value);

  boolean typestate_canCastTo(AlligatorusType prototype);

  MortarDataType typestate_asType();

  /** Returns null if error. */
  MortarDataTypestate typestate_unfork(
      EvaluationContext context,
      Location location,
      MortarDataTypestate other,
      Location otherLocation);
}
