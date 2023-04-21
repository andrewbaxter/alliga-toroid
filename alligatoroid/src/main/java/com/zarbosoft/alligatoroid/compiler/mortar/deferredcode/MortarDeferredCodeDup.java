package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarDeferredCodeDup implements MortarDeferredCode {
  @Override
  public JavaBytecode drop() {
    return null;
  }

  @Override
  public JavaBytecode consume() {
    return Global.JBC_DUP;
  }

  @Override
  public JavaBytecode set(JavaBytecode valueCode) {
    throw new Assertion();
  }
}
