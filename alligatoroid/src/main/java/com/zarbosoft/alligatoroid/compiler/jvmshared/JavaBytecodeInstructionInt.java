package com.zarbosoft.alligatoroid.compiler.jvmshared;

import org.objectweb.asm.MethodVisitor;

public class JavaBytecodeInstructionInt implements JavaBytecodeInstruction {
  public final int code;

  public JavaBytecodeInstructionInt(int code) {
    this.code = code;
  }

  @Override
  public void write(MethodVisitor out) {
  out.visitInsn(code);
  }
}
