package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;

public class JVMObjectType implements JVMBaseObjectType, SingletonBuiltinExportable {
  public static JVMObjectType type = new JVMObjectType();

  protected JVMObjectType() {}

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.OBJECT;
  }
}
