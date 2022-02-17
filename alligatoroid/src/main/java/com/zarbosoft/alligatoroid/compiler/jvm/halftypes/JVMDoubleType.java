package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;

import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DSTORE;

public class JVMDoubleType implements JVMType, SingletonBuiltinExportable {
  public static final JVMDoubleType value = new JVMDoubleType();

  private JVMDoubleType() {}

  @Override
  public int storeOpcode() {
    return DSTORE;
  }

  @Override
  public int loadOpcode() {
    return DLOAD;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.DOUBLE;
  }
}
