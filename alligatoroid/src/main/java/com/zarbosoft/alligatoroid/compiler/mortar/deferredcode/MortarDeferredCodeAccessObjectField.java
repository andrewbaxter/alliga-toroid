package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;

public class MortarDeferredCodeAccessObjectField implements MortarDeferredCode {
  public final MortarDeferredCode base;
  public final JavaInternalName klass;
  public final String field;
  public final JavaDataDescriptor type;

  public MortarDeferredCodeAccessObjectField(
      MortarDeferredCode base, JavaInternalName klass, String field, JavaDataDescriptor type) {
    this.base = base;
    this.klass = klass;
    this.field = field;
    this.type = type;
  }

  @Override
  public JavaBytecode drop() {
    return base.drop();
  }

  @Override
  public JavaBytecode consume() {
    return JavaBytecodeUtils.seq()
        .add(base.consume())
        .add(JavaBytecodeUtils.accessField(-1, klass, field, type));
  }

  @Override
  public JavaBytecode set(JavaBytecode value) {
    return JavaBytecodeUtils.seq()
        .add(base.consume())
        .add(value)
        .add(JavaBytecodeUtils.setField(-1, klass, field, type));
  }
}
