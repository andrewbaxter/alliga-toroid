package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.rendaw.common.ROList;

public class Tuple {
  @Exportable.Param public ROList<Object> data;

  public Tuple(ROList<Object> data) {
    this.data = data;
  }

  public Object get(int index) {
    return data.get(index);
  }
}
