package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;
import com.zarbosoft.rendaw.common.TSList;

/** Like a.b.c */
public class JavaQualifiedName implements AutoExportable {
  @AutoExporter.Param
  public TSList<String> value;

  public String toString() {
    final StringBuilder out = new StringBuilder();
    boolean first = true;
    for (String s : value) {
      if (first) {
          first = false;
      } else {
          out.append(".");
      }
      out.append(s);
    }
    return out.toString();
  }

  @Override
  public void postInit() {}

  @StaticAutogen.WrapExpose
  public JavaInternalName asInternalName() {
    final StringBuilder out = new StringBuilder();
    boolean first = true;
    for (String s : value) {
      if (first) {
          first = false;
      } else {
          out.append("$");
      }
      out.append(s.replace('.', '/'));
    }
    return JavaBytecodeUtils.internalName(out.toString());
  }
}
