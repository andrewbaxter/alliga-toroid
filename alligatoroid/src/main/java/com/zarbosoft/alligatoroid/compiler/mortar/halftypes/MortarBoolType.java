package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.rendaw.common.TSList;

public class MortarBoolType implements MortarPrimitiveType {
  public static final MortarBoolType type = new MortarBoolType();

  private MortarBoolType() {}

  @Override
  public EvaluateResult type_valueVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.pure(type_stackAsValue(JavaBytecodeUtils.literalBool((Boolean) data)));
  }

  @Override
  public JavaBytecode type_storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeIntShortByteBool(key);
  }

  @Override
  public JavaBytecode type_arrayStoreBytecode() {
    return JavaBytecodeUtils.arrayStoreByteBool;
  }

  @Override
  public JavaBytecode type_arrayLoadBytecode() {
    return JavaBytecodeUtils.arrayLoadByteBool;
  }

  @Override
  public JavaBytecode type_loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadIntShortByteBool(key);
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return JavaBytecodeUtils.returnIntShortByteBool;
  }

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return jvmDesc();
  }

  private static JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.BOOL;
  }

  @Override
  public JavaDataDescriptor tuple_fieldtype_jvmDesc() {
    return jvmDesc();
  }
}
