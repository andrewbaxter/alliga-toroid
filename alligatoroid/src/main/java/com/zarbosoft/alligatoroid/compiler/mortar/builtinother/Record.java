package com.zarbosoft.alligatoroid.compiler.mortar.builtinother;

import com.zarbosoft.rendaw.common.ROMap;

public final class Record {
  // TODO this should be ordered, and auto serialization should blow up if it encounters unordered
  // collections
  public final ROMap<Object, Object> data;

  public Record(ROMap<Object, Object> data) {
    this.data = data;
  }

  public Object get(Object key) {
    return data.get(key);
  }
}
