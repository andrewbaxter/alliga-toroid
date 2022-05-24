package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Artifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.CallNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import static org.objectweb.asm.Opcodes.POP;

public interface MortarDataType extends Artifact {
  public static boolean assertAssignableFromUnion(
      EvaluationContext context,
      Location location,
      MortarDataType receiveType,
      MortarDataType... types) {
    TSList<Error> errors = new TSList<>();
    for (MortarDataType type : types) {
      if (type.checkAssignableFrom(errors, location, receiveType, new TSList<>())) return true;
    }
    context.errors.addAll(errors);
    return false;
  }

  default EvaluateResult variableValueAccess(
          EvaluationContext context, Location location, MortarDeferredCode base, Value field) {
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  default VariableDataValue forkBinding(MortarDataBinding binding) {
    return new VariableDataValue(
        binding.type.fork(),
        new MortarDeferredCodeBinding(loadBytecode(binding.key), storeBytecode(binding.key)));
  }

  default MortarDataType fork() {
    return this;
  }

  default ROPair<TargetCode, Binding> varValueBind(EvaluationContext context, TargetCode code) {
    JavaBytecodeBindingKey key = new JavaBytecodeBindingKey();
    return new ROPair<>(
        new MortarTargetCode(
            new JavaBytecodeSequence().add(((MortarTargetCode) code).e).add(storeBytecode(key))),
        new MortarDataBinding(key, this));
  }

  JavaDataDescriptor jvmDesc();

  JavaBytecode returnBytecode();

  JavaBytecode storeBytecode(JavaBytecodeBindingKey key);

  JavaBytecode loadBytecode(JavaBytecodeBindingKey key);

  JavaBytecode arrayStoreBytecode();

  JavaBytecode arrayLoadBytecode();

  default VariableDataValue deferredStackAsValue(JavaBytecode code) {
    return new VariableDataValue(MortarCarry.ofDeferredHalf(c -> code), this);
  }

  default VariableDataValue stackAsValue(JavaBytecode code) {
    return new VariableDataValue(MortarCarry.ofHalf(code, JavaBytecodeUtils.inst(POP)), this);
  }

  default Value constAsValue(Object value) {
    return ConstDataValue.create(this, value);
  }

  default EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object value, Value field) {
    // Duplicated in Value
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path);

  default boolean checkAssignableFrom(Location location, Value value) {
    if (!(value instanceof DataValue)) return false;
    return checkAssignableFrom(
        new TSList<>(), location, ((DataValue) value).mortarType(), new TSList<>());
  }

  default boolean assertAssignableFrom(
      EvaluationContext context, Location location, MortarDataType type) {
    return checkAssignableFrom(context.moduleContext.getErrors(), location, type, new TSList<>());
  }

  default boolean assertAssignableFrom(TSList<Error> errors, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      errors.add(new WrongType(location, new TSList<>(), value.toString(), "data value"));
    }
    return checkAssignableFrom(errors, location, ((DataValue) value).mortarType(), new TSList<>());
  }

  default EvaluateResult constCall(
      EvaluationContext context, Location location, Object inner, Value argument) {
    context.errors.add(new CallNotSupported(location));
    return EvaluateResult.error;
  }

  default ROList<String> traceFields(EvaluationContext context, Location location, Object inner) {
    return ROList.empty;
  }

  SemiserialSubvalue graphSemiserializeValue(
      Object inner,
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Artifact> path,
      ROList<String> accessPath);

  Object graphDesemiserializeValue(ModuleCompileContext context, SemiserialSubvalue data);

  EvaluateResult valueVary(EvaluationContext context, Location id, Object data);
}
