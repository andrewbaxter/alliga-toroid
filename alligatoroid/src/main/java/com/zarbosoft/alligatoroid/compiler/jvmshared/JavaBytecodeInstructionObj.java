package com.zarbosoft.alligatoroid.compiler.jvmshared;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;

public class JavaBytecodeInstructionObj implements JavaBytecodeInstruction {
  private final AbstractInsnNode node;

  public JavaBytecodeInstructionObj(AbstractInsnNode node) {
    this.node = node;
  }

  @Override
  public void write(MethodVisitor out) {
    node.accept(out);
  }
}
