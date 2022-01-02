package com.zarbosoft.alligatoroid.compiler.jvmshared;

public interface JVMSharedCodeElement {
  void dispatch(Dispatcher dispatcher);

  interface Dispatcher {
    void handleNested(JVMSharedCode<?> code);

    void handleStoreLoad(JVMSharedCodeStoreLoad storeLoad);

    void handleInstruction(JVMSharedCodeInstruction instruction);
  }
}
