package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.BindingKey;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.CallNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataStackValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableBoundDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import static org.objectweb.asm.Opcodes.POP;

public interface MortarDataType extends Exportable {
  public static boolean assertAssignableFromUnion(
      EvaluationContext context,
      Location location,
      MortarDataType receiveType,
      MortarDataType... types) {
    TSList<Error> errors = new TSList<>();
    for (MortarDataType type : types) {
      if (type.checkAssignableFrom(errors, location, receiveType, new TSList<>())) return true;
    }
    context.moduleContext.errors.addAll(errors);
    return false;
  }

  default EvaluateResult variableValueAccess(
      EvaluationContext context, Location location, MortarCarry targetCarry, Value field) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  default VariableBoundDataValue boundAsValue(MortarDataBinding binding) {
    return new VariableBoundDataValue(binding);
  }

  default ROPair<TargetCode, Binding> varValueBind(EvaluationContext context, MortarCarry lower) {
    BindingKey key = new BindingKey();
    return new ROPair<>(
        new JVMSharedCode().add(lower.half(context)).addVarInsn(storeOpcode(), key),
        new MortarDataBinding(key, this));
  }

  JVMSharedDataDescriptor jvmDesc();

  int returnOpcode();

  int storeOpcode();

  int loadOpcode();

  default VariableDataStackValue deferredStackAsValue(JVMSharedCodeElement code) {
    return new VariableDataStackValue(MortarCarry.ofDeferredHalf(c -> code), this);
  }

  default VariableDataStackValue stackAsValue(JVMSharedCodeElement code) {
    return new VariableDataStackValue(MortarCarry.ofHalf(code, JVMSharedCode.inst(POP)), this);
  }

  default ConstDataBuiltinSingletonValue constBuiltinSingletonAsValue(Object value) {
    return new ConstDataBuiltinSingletonValue(this, value);
  }

  default Value constAsValue(Object value) {
    final ConstDataStackValue out = new ConstDataStackValue();
    out.type = this;
    out.value = value;
    return out;
  }

  JVMSharedCodeElement constValueVary(EvaluationContext context, Object value);

  default EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object value, Value field) {
    // Duplicated in Value
    context.moduleContext.errors.add(new AccessNotSupported(location));
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
    return checkAssignableFrom(context.moduleContext.errors, location, type, new TSList<>());
  }

  default boolean assertAssignableFrom(EvaluationContext context, Location location, Value value) {
    if (!(value instanceof DataValue)) {
      context.moduleContext.errors.add(
          new WrongType(location, new TSList<>(), value.toString(), "data value"));
    }
    return checkAssignableFrom(
        context.moduleContext.errors, location, ((DataValue) value).mortarType(), new TSList<>());
  }

  default EvaluateResult constCall(
      EvaluationContext context, Location location, Object inner, Value argument) {
    context.moduleContext.errors.add(new CallNotSupported(location));
    return EvaluateResult.error;
  }

  default ROList<String> traceFields(Object inner) {
    return ROList.empty;
  }
}
