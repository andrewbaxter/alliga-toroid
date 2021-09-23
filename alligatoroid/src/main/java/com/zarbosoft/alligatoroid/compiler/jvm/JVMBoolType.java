package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;

public class JVMBoolType extends JVMObjectType implements SimpleValue {
  public static final JVMBoolType value = new JVMBoolType();

  private JVMBoolType() {}

  @Override
  public String jvmDesc() {
    return JVMDescriptor.boolDescriptor;
  }
}
