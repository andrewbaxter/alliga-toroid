package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;

public class MortarDeferredCodeBinding implements MortarDeferredCode {
  public final JavaBytecode load;
  public final JavaBytecode store;

  public MortarDeferredCodeBinding(JavaBytecode load, JavaBytecode store) {
    this.load = load;
    this.store = store;
  }

  @Override
  public JavaBytecode drop() {
    return null;
  }

  @Override
  public JavaBytecode consume() {
    return load;
  }

  @Override
  public JavaBytecode set(JavaBytecode valueCode) {
    return new JavaBytecodeSequence().add(valueCode).add(store);
  }
}
