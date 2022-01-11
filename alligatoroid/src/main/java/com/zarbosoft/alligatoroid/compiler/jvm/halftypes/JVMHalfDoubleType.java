package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;

import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DSTORE;

public class JVMHalfDoubleType implements JVMHalfDataType {
  public static final JVMHalfDoubleType value = new JVMHalfDoubleType();

  private JVMHalfDoubleType() {}

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
