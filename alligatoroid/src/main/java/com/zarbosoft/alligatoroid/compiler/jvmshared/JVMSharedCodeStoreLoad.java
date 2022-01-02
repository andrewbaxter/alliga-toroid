package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.rendaw.common.Assertion;

class JVMSharedCodeStoreLoad implements JVMSharedCodeElement {
  final int code;
  final Object key;

  public JVMSharedCodeStoreLoad(int code, Object key) {
    if (key == null) {
      throw new Assertion();
    }
    this.code = code;
    this.key = key;
  }

  @Override
  public void dispatch(Dispatcher dispatcher) {
    dispatcher.handleStoreLoad(this);
  }
}
