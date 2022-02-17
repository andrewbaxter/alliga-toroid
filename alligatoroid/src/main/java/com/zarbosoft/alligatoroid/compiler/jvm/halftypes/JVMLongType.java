package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;

import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;

public class JVMLongType implements JVMType, SingletonBuiltinExportable {
  public static final JVMLongType value = new JVMLongType();

  private JVMLongType() {}

  @Override
  public int storeOpcode() {
    return LSTORE;
  }

  @Override
  public int loadOpcode() {
    return LLOAD;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.LONG;
  }
}
