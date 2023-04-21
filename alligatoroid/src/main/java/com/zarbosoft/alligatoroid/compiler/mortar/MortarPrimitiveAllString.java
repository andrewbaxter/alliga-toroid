package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;

public class MortarPrimitiveAllString implements MortarPrimitiveAll.Inner {
  public static final MortarPrimitiveAllString instance = new MortarPrimitiveAllString();

  private MortarPrimitiveAllString() {}

  @Override
  public JavaDataDescriptor jvmDesc() {
    return Global.DESC_STRING;
  }

  @Override
  public JavaBytecode returnBytecode() {
    return Global.JBC_returnObj;
  }

  @Override
  public JavaBytecode storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeObj(key);
  }

  @Override
  public JavaBytecode loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadObj(key);
  }

  @Override
  public JavaBytecode arrayLoadBytecode() {
    return Global.JBC_ARRAY_LOAD_OBJ;
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
    return Global.JBC_ARRAY_STORE_OBJ;
  }

  @Override
  public JavaBytecode literalBytecode(Object constData) {
    return JavaBytecodeUtils.literalString((String) constData);
  }

  @Override
  public JavaBytecode fromObj() {
    return JavaBytecodeUtils.cast(jvmDesc());
  }

  @Override
  public JavaBytecode toObj() {
    return null;
  }
}
