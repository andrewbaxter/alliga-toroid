package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;

public class JavaBytecodeInstruction implements JavaBytecode, AutoBuiltinExportable {
  public final int code;

  public JavaBytecodeInstruction(int code) {
    this.code = code;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleInstruction(this);
  }
}
