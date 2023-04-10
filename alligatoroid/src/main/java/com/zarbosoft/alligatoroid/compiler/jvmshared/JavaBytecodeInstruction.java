package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import org.objectweb.asm.MethodVisitor;

public interface JavaBytecodeInstruction extends JavaBytecode, BuiltinAutoExportable {
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
