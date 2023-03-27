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
import org.jetbrains.annotations.NotNull;

public class MortarByteType implements MortarPrimitiveType {
  public static final MortarByteType type = new MortarByteType();

  private MortarByteType() {}

  @Override
  public EvaluateResult type_valueVary(EvaluationContext context, Location id, Object data) {
    return EvaluateResult.pure(
        type_stackAsValue(JavaBytecodeUtils.literalIntShortByte((Byte) data)));
  }

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return jvmDesc();
  }

  @NotNull
  private static JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.BYTE;
  }

  @Override
  public JavaBytecode type_returnBytecode() {
    return JavaBytecodeUtils.returnIntShortByteBool;
  }

  @Override
  public JavaBytecode type_storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeIntShortByteBool(key);
  }

  @Override
  public JavaBytecode type_loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadIntShortByteBool(key);
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
  public JavaDataDescriptor tuple_fieldtype_jvmDesc() {
    return jvmDesc();
  }
}
