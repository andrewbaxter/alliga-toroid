package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;
import com.zarbosoft.rendaw.common.Assertion;

public class JVMNullType implements JVMType, SingletonBuiltinExportable {
  public static final JVMNullType type = new JVMNullType();

  private JVMNullType() {}

  @Override
  public int storeOpcode() {
    throw new Assertion();
  }

  @Override
  public int loadOpcode() {
    throw new Assertion();
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.VOID;
  }
}
