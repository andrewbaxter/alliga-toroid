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

public class MortarPrimitiveAllInt implements MortarPrimitiveAll.Inner {
  public static final MortarPrimitiveAllInt instance = new MortarPrimitiveAllInt();

  private MortarPrimitiveAllInt() {}

  @Override
  public JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.INT;
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
  public JavaBytecode arrayLoadBytecode() {
    return JavaBytecodeUtils.arrayLoadInt;
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
    return JavaBytecodeUtils.arrayStoreInt;
  }

  @Override
  public JavaBytecode literalBytecode(Object constData) {
    return JavaBytecodeUtils.literalIntShortByte((Integer) constData);
  }

  @Override
  public JavaBytecode fromObj() {
    return new JavaBytecodeSequence()
        .add(JavaBytecodeUtils.cast(JavaDataDescriptor.BOXED_INT))
        .add(
            JavaBytecodeUtils.callMethod(
                -1,
                JavaInternalName.BOXED_INT,
                "integerValue",
                JavaMethodDescriptor.fromParts(JavaDataDescriptor.INT, ROList.empty)));
  }

  @Override
  public JavaBytecode toObj() {
    return JavaBytecodeUtils.callStaticMethod(
        -1,
        JavaInternalName.BOXED_INT,
        "valueOf",
        JavaMethodDescriptor.fromParts(
            JavaDataDescriptor.BOXED_INT, TSList.of(JavaDataDescriptor.INT)));
  }
}
