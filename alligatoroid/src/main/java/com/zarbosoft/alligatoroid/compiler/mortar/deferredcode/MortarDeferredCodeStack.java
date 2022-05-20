package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarDeferredCodeStack implements MortarDeferredCode {
  private final JavaBytecode code;

  public MortarDeferredCodeStack(JavaBytecode code) {
    this.code = code;
  }

  @Override
  public JavaBytecode drop() {
    return JavaBytecodeUtils.seq().add(code).add(JavaBytecodeUtils.dup);
  }

  @Override
  public JavaBytecode consume() {
    return code;
  }

  @Override
  public JavaBytecode set(JavaBytecode value) {
    throw new Assertion();
  }
}
