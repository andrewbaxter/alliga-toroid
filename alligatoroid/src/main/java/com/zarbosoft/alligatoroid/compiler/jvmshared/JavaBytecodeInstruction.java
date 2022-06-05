package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;

public class JavaBytecodeInstruction implements JavaBytecode, BuiltinAutoExportable {
  public final int code;

  public JavaBytecodeInstruction(int code) {
    this.code = code;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleInstruction(this);
  }
}
