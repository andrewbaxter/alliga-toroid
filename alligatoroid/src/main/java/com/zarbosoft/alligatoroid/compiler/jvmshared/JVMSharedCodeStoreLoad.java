package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.BindingKey;
import com.zarbosoft.rendaw.common.Assertion;

public class JVMSharedCodeStoreLoad implements JVMSharedCodeElement {
  final int code;
  final Object key;

  public JVMSharedCodeStoreLoad(int code, BindingKey key) {
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
