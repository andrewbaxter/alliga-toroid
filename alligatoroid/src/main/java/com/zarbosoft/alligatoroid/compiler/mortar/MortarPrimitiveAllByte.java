package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class MortarPrimitiveAllByte implements MortarPrimitiveAll.Inner {
  public static MortarPrimitiveAll.Inner instance = new MortarPrimitiveAllByte();

  @Override
  public JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.BYTE;
  }

  @Override
  public JavaBytecode returnBytecode() {
    return JavaBytecodeUtils.returnIntShortByteBool;
  }

  @Override
  public JavaBytecode storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeIntShortByteBool(key);
  }

  @Override
  public JavaBytecode loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadIntShortByteBool(key);
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
    return JavaBytecodeUtils.arrayStoreByteBool;
  }

  @Override
  public JavaBytecode literalBytecode(Object constData) {
    return JavaBytecodeUtils.literalIntShortByte((Byte) constData);
  }

  @Override
  public JavaBytecode arrayLoadBytecode() {
    return JavaBytecodeUtils.arrayLoadByteBool;
  }

  @Override
  public JavaBytecode fromObj() {
    return new JavaBytecodeSequence()
        .add(JavaBytecodeUtils.cast(JavaDataDescriptor.BOXED_BYTE))
        .add(
            JavaBytecodeUtils.callMethod(
                -1,
                JavaInternalName.BOXED_BYTE,
                "byteValue",
                JavaMethodDescriptor.fromParts(JavaDataDescriptor.BYTE, ROList.empty)));
  }

  @Override
  public JavaBytecode toObj() {
    return JavaBytecodeUtils.callStaticMethod(
        -1,
        JavaInternalName.BOXED_BYTE,
        "valueOf",
        JavaMethodDescriptor.fromParts(
            JavaDataDescriptor.BOXED_BYTE, TSList.of(JavaDataDescriptor.BYTE)));
  }
}
