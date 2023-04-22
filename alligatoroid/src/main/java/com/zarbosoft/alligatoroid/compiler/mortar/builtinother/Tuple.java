package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;
import com.zarbosoft.rendaw.common.ROList;

public class Tuple implements AutoExportable {
  @AutoExporter.Param
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
