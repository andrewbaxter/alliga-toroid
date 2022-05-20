package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.rendaw.common.ROList;

public class Tuple implements AutoBuiltinExportable {
  @AutoBuiltinExportableType.Param
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
