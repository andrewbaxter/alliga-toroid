package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;

import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;

public class JVMHalfLongType implements JVMHalfDataType {
  public static final JVMHalfLongType value = new JVMHalfLongType();

  private JVMHalfLongType() {}

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
