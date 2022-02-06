package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;

public class JVMHalfIntType implements JVMHalfDataType {
  public static final JVMHalfIntType type = new JVMHalfIntType();

  private JVMHalfIntType() {}

  @Override
  public int storeOpcode() {
    return ISTORE;
  }

  @Override
  public int loadOpcode() {
    return ILOAD;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.INT;
  }
}
