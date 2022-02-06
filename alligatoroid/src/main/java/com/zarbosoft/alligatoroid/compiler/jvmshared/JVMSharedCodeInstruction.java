package com.zarbosoft.alligatoroid.compiler.jvmshared;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

import static org.objectweb.asm.Opcodes.ACONST_NULL;

public class JVMSharedCodeInstruction implements JVMSharedCodeElement {
  public static JVMSharedCodeInstruction null_ =
      new JVMSharedCodeInstruction(new InsnNode(ACONST_NULL));
  public final AbstractInsnNode node;

  public JVMSharedCodeInstruction(AbstractInsnNode node) {
    this.node = node;
  }

  @Override
  public void dispatch(Dispatcher dispatcher) {
    dispatcher.handleInstruction(this);
  }
}
