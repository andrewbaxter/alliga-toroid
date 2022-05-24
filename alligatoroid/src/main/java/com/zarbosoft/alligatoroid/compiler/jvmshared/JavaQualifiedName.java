package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinArtifact;
import com.zarbosoft.rendaw.common.TSList;

/** Like a.b.c */
public class JavaQualifiedName implements AutoBuiltinArtifact {
  @Param public TSList<String> value;

  public String toString() {
    final StringBuilder out = new StringBuilder();
    boolean first = true;
    for (String s : value) {
      if (first) first = false;
      else out.append(".");
      out.append(s);
    }
    return out.toString();
  }

  @Override
  public void postInit() {}

  @Meta.WrapExpose
  public JavaInternalName asInternalName() {
    final StringBuilder out = new StringBuilder();
    boolean first = true;
    for (String s : value) {
      if (first) first = false;
      else out.append("$");
      out.append(s.replace('.', '/'));
    }
    return JavaBytecodeUtils.internalName(out.toString());
  }
}
