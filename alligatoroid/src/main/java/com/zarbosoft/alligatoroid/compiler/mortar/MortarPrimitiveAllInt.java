package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import org.jetbrains.annotations.NotNull;

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
}
