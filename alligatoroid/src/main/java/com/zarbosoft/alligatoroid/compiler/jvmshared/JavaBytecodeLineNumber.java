package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;

public class JavaBytecodeLineNumber implements JavaBytecode, AutoBuiltinExportable {
  public int lineNumber;

  public static JavaBytecodeLineNumber create(int number) {
    final JavaBytecodeLineNumber out = new JavaBytecodeLineNumber();
    out.lineNumber = number;
    out.postInit();
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleLineNumber(this);
  }
}
