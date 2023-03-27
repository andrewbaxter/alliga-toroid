package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.VaryNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.Assertion;
import org.jetbrains.annotations.NotNull;

public class MortarNullType implements MortarSimpleDataType {
  public static final MortarNullType type = new MortarNullType();

  private MortarNullType() {}

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return jvmDesc();
  }

  @NotNull
  private static JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.VOID;
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return JavaBytecodeUtils.returnVoid;
  }

  @Override
  public JavaBytecode type_storeBytecode(JavaBytecodeBindingKey key) {
    throw new Assertion();
  }

  @Override
  public JavaBytecode type_loadBytecode(JavaBytecodeBindingKey key) {
    throw new Assertion();
  }

  @Override
  public JavaBytecode type_arrayStoreBytecode() {
    throw new Assertion();
  }

  @Override
  public JavaBytecode type_arrayLoadBytecode() {
    throw new Assertion();
  }

  @Override
  public EvaluateResult type_valueVary(EvaluationContext context, Location id, Object data) {
    context.errors.add(new VaryNotSupported(id));
    return EvaluateResult.error;
  }

  @Override
  public JavaBytecode type_castTo(MortarDataPrototype prototype, MortarDeferredCode code) {
  throw new Assertion();
  }

  @Override
  public boolean type_canCastTo(MortarDataPrototype prototype) {
  return false;
  }

  @Override
  public JavaDataDescriptor tuple_fieldtype_jvmDesc() {
  return jvmDesc();
  }
}
