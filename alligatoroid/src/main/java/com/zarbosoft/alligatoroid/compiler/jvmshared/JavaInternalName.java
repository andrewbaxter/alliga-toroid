package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;

/** Like a/b/c */
public class JavaInternalName implements BuiltinAutoExportable {
  @BuiltinAutoExporter.Param
  public String value;

  public String toString() {
    return value;
  }

  @Override
  public void postInit() {}
}
