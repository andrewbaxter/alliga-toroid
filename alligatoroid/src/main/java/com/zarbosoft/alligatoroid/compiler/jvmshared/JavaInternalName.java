package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;

/** Like a/b/c */
public class JavaInternalName implements AutoExportable {
  @AutoExporter.Param
  public String value;

  public String toString() {
    return value;
  }

  @Override
  public void postInit() {}
}
