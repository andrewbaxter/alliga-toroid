package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarDeferredCodeStack implements MortarDeferredCode {

  public MortarDeferredCodeStack() {}

  @Override
  public JavaBytecode drop() {
    return JavaBytecodeUtils.pop;
  }

  @Override
  public JavaBytecode consume() {
    return null;
  }

  @Override
  public JavaBytecode set(JavaBytecode valueCode) {
    throw new Assertion();
  }
}
