package com.zarbosoft.alligatoroid.compiler.jvmshared;

import org.objectweb.asm.Label;

public class JavaBytecodeInstructionTryCatch implements JavaBytecodeInstruction {
  public final Label start;
  public final Label end;
  public final Label handler;
  public final String excType;

  public JavaBytecodeInstructionTryCatch(
          Label start, Label end, Label handler, String excType) {
    this.start = start;
    this.end = end;
    this.handler = handler;
    this.excType = excType;
  }

  @Override
  public void dispatchMore(MoreDispatcher moreDispatcher) {
    moreDispatcher.handleTryCatch(this);
  }
}
