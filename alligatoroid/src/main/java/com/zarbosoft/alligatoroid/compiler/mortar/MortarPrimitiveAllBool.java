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

public class MortarPrimitiveAllBool implements MortarPrimitiveAll.Info {
  public static final MortarPrimitiveAllBool instance = new MortarPrimitiveAllBool();

  private MortarPrimitiveAllBool() {}

  public JavaDataDescriptor jvmDesc() {
    return Global.DESC_BOOL;
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
    return Global.JBC_arrayLoadByteBool;
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
    return Global.JBC_arrayStoreByteBool;
  }

  @Override
  public JavaBytecode literalBytecode(Object constData) {
    return JavaBytecodeUtils.literalBool((Boolean) constData);
  }

  @Override
  public JavaBytecode fromObj() {
    return new JavaBytecodeSequence()
        .add(JavaBytecodeUtils.cast(Global.DESC_BOXED_BOOL))
        .add(
            JavaBytecodeUtils.callMethod(
                -1,
                Global.INTNAME_BOXED_BOOL,
                "booleanValue",
                JavaMethodDescriptor.fromParts(Global.DESC_BOOL, ROList.empty)));
  }

  @Override
  public JavaBytecode toObj() {
    return JavaBytecodeUtils.callStaticMethod(
        -1,
        Global.INTNAME_BOXED_BOOL,
        "valueOf",
        JavaMethodDescriptor.fromParts(
            Global.DESC_BOXED_BOOL, TSList.of(Global.DESC_BOOL)));
  }
}
