package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;

/** Like a.b.c */
public class JVMSharedNormalName implements AutoBuiltinExportable {
  @Param public String value;

  public static JVMSharedNormalName fromString(String name) {
    final JVMSharedNormalName out = new JVMSharedNormalName();
    out.value = name;
    return out;
  }

  public String toString() {
    return value;
  }

  @Override
  public void postInit() {}
}
