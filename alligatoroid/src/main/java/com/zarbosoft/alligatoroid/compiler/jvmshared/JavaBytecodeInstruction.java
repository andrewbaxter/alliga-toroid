package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;

public interface JavaBytecodeInstruction extends JavaBytecode, AutoExportable {
  default <T> T dispatch(JavaBytecode.Dispatcher<T> dispatcher) {
    return dispatcher.handleInstruction(this);
  }

  void dispatchMore(MoreDispatcher moreDispatcher);

  public interface MoreDispatcher {
    void handleObj(JavaBytecodeInstructionObj n);

    void handleInt(JavaBytecodeInstructionInt n);

    void handleTryCatch(JavaBytecodeInstructionTryCatch n);
  }
}
