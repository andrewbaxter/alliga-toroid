package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;

public class JVMHalfStringType extends JVMHalfObjectType {
  public static final JVMHalfStringType value = new JVMHalfStringType();

  private JVMHalfStringType() {}

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.STRING;
  }
}
