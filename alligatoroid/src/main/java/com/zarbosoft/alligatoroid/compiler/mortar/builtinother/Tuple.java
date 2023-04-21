package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExporter;
import com.zarbosoft.rendaw.common.ROList;

public class Tuple implements BuiltinAutoExportable {
  @BuiltinAutoExporter.Param
  public ROList<Object> data;

  public Tuple() {}

  public static Tuple create(ROList<Object> data) {
    final Tuple out = new Tuple();
    out.data = data;
    out.postInit();
    return out;
  }

  public Object get(int index) {
    return data.get(index);
  }
}
