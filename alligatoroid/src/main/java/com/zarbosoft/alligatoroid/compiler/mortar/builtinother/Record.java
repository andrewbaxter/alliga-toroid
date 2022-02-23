package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.rendaw.common.ROMap;

public final class Record implements AutoBuiltinExportable {
  // TODO this should be ordered, and auto serialization should blow up if it encounters unordered
  // collections
  @Exportable.Param public ROMap<Object, Object> data;

  public Record() {}

  public static Record create(ROMap<Object, Object> data) {
    final Record out = new Record();
    out.data = data;
    out.postInit();
    return out;
  }

  public Object get(Object key) {
    return data.get(key);
  }
}
