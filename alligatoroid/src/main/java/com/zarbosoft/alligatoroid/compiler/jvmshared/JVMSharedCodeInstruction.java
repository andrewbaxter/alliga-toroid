package com.zarbosoft.alligatoroid.compiler.jvmshared;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

public class JVMSharedCodeInstruction implements JVMSharedCodeElement {
  public final AbstractInsnNode node;

  public JVMSharedCodeInstruction(AbstractInsnNode node) {
    this.node = node;
  }

  @Override
  public void dispatch(Dispatcher dispatcher) {
    dispatcher.handleInstruction(this);
  }
}
