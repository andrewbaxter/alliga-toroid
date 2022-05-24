package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Artifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinArtifact;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class MortarNullType implements MortarSimpleDataType, SingletonBuiltinArtifact {
  public static final MortarNullType type = new MortarNullType();

  private MortarNullType() {}

  @Override
  public JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.VOID;
  }

  @Override
  public JavaBytecode returnBytecode() {
  return JavaBytecodeUtils.returnVoid;
  }

  @Override
  public JavaBytecode storeBytecode(JavaBytecodeBindingKey key) {
    throw new Assertion();
  }

  @Override
  public JavaBytecode loadBytecode(JavaBytecodeBindingKey key) {
    throw new Assertion();
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
    throw new Assertion();
  }

  @Override
  public JavaBytecode arrayLoadBytecode() {
    throw new Assertion();
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (type != this.type) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }

  @Override
  public SemiserialSubvalue graphSemiserializeValue(Object inner, long importCacheId, Semiserializer semiserializer, ROList<Artifact> path, ROList<String> accessPath) {
    throw new Assertion();
  }

  @Override
  public Object graphDesemiserializeValue(ModuleCompileContext context, SemiserialSubvalue data) {
    throw new Assertion();
  }

  @Override
  public EvaluateResult valueVary(EvaluationContext context, Location id, Object data) {
  context.errors.add(new CantVaryNull);
  return EvaluateResult.error;
  }
}
