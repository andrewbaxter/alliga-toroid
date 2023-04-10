package com.zarbosoft.alligatoroid.compiler.jvmshared;

public class JavaBytecodeInstructionInt implements JavaBytecodeInstruction {
  public final int code;

  public JavaBytecodeInstructionInt(int code) {
    this.code = code;
  }

  @Override
  public void dispatchMore(MoreDispatcher moreDispatcher) {
  moreDispatcher.handleInt(this);
  }
}
