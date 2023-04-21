package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarDeferredCodeStack implements MortarDeferredCode {

  public MortarDeferredCodeStack() {}

  @Override
  public JavaBytecode drop() {
    return Global.JBC_POP;
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
