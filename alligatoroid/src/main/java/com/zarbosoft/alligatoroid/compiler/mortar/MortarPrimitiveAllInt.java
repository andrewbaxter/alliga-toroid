package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

public class MortarPrimitiveAllInt implements MortarPrimitiveAll.Inner {
  public static final MortarPrimitiveAllInt instance = new MortarPrimitiveAllInt();

  private MortarPrimitiveAllInt() {}

  @Override
  public JavaDataDescriptor jvmDesc() {
    return Global.DESC_INT;
  }

  @Override
  public JavaBytecode returnBytecode() {
    return Global.JBC_returnIntShortByteBool;
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
    return Global.JBC_ARRAY_LOAD_INT;
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
    return Global.JBC_ARRAY_STORE_INT;
  }

  @Override
  public JavaBytecode literalBytecode(Object constData) {
    return JavaBytecodeUtils.literalIntShortByte((Integer) constData);
  }

  @Override
  public JavaBytecode fromObj() {
    return new JavaBytecodeSequence()
        .add(JavaBytecodeUtils.cast(Global.DESC_BOXED_INT))
        .add(
            JavaBytecodeUtils.callMethod(
                -1,
                Global.INTNAME_BOXED_INT,
                "integerValue",
                JavaMethodDescriptor.fromParts(Global.DESC_INT, ROList.empty)));
  }

  @Override
  public JavaBytecode toObj() {
    return JavaBytecodeUtils.callStaticMethod(
        -1,
        Global.INTNAME_BOXED_INT,
        "valueOf",
        JavaMethodDescriptor.fromParts(
            Global.DESC_BOXED_INT, TSList.of(Global.DESC_INT)));
  }
}
