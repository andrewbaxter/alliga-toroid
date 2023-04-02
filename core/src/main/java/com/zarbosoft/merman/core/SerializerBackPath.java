package com.zarbosoft.merman.core;

import com.zarbosoft.rendaw.common.TSList;

public class SerializerBackPath {
  public static final SerializerBackPath root = new SerializerBackPath(null, -1);
  public final SerializerBackPath parent;
  public final int index;

  public SerializerBackPath(SerializerBackPath parent, int index) {
    this.parent = parent;
    this.index = index;
  }

  public SerializerBackPath add(int index) {
    return new SerializerBackPath(this, index);
  }

  public String toString() {
    TSList<String> list = new TSList<>();
    SerializerBackPath at = this;
    while (at != null && at != root) {
      list.add(Integer.toString(at.index));
      at = at.parent;
    }
    list.reverse();
    StringBuilder out = new StringBuilder();
    for (String i : list) {
      out.append("/");
      out.append(i);
    }
    if (out.length() == 0) {
        out.append("/");
    }
    return out.toString();
  }
}
