package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.SetNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public final class MortarImmutableType implements MortarDataType, AutoBuiltinExportable {
  public static final MortarImmutableType nullType = new MortarImmutableType(MortarNullType.type);
  public static final MortarImmutableType intType = new MortarImmutableType(MortarIntType.type);
  public static final MortarImmutableType boolType = new MortarImmutableType(MortarBoolType.type);
  public static final MortarImmutableType stringType =
      new MortarImmutableType(MortarStringType.type);

  public final MortarDataType innerType;

  public MortarImmutableType(MortarDataType innerType) {
    this.innerType = innerType;
  }


  @Override
  public JavaDataDescriptor jvmDesc() {
    return innerType.jvmDesc();
  }

  @Override
  public JavaBytecode returnBytecode() {
  return innerType.returnBytecode();
  }

  @Override
  public JavaBytecode storeBytecode(JavaBytecodeBindingKey key) {
  return innerType.storeBytecode(key);
  }

  @Override
  public JavaBytecode loadBytecode(JavaBytecodeBindingKey key) {
  return innerType.loadBytecode(key);
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
  return innerType.arrayStoreBytecode();
  }

  @Override
  public JavaBytecode arrayLoadBytecode() {
  return innerType.arrayLoadBytecode();
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    errors.add(new SetNotSupported(location));
    return false;
  }

  @Override
  public SemiserialSubvalue graphSemiserializeValue(Object inner, long importCacheId, Semiserializer semiserializer, ROList<Exportable> path, ROList<String> accessPath) {
    throw new Assertion();
  }

  @Override
  public Object graphDesemiserializeValue(ModuleCompileContext context, SemiserialSubvalue data) {
    throw new Assertion();
  }

  @Override
  public EvaluateResult valueVary(EvaluationContext context, Location id, Object data) {
  }

  @Override
  public Class jvmClass() {
  return innerType.jvmClass();
  }
}
