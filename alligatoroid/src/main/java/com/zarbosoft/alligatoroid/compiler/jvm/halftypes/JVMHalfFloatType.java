package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;

import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;

public class JVMHalfFloatType implements JVMHalfDataType {
  public static final JVMHalfFloatType value = new JVMHalfFloatType();

  private JVMHalfFloatType() {}

  @Override
  public int storeOpcode() {
    return FSTORE;
  }

  @Override
  public int loadOpcode() {
    return FLOAD;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.FLOAT;
  }
}
