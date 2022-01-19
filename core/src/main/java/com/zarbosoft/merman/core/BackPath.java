package com.zarbosoft.merman.core;

import com.zarbosoft.rendaw.common.ROList;

public class BackPath {
  /** Offset, true=key,false=value (false except in records) */
  public final ROList<Element> segments;

  public BackPath(final ROList<Element> segments) {
    this.segments = segments;
  }

  public ROList<Element> toList() {
    return segments;
  }

  public static class Element {
    public final int index;
    public final int typeCount;
    public final boolean key;

    public Element(int index, boolean key, int typeCount) {
      this.index = index;
      this.typeCount = typeCount;
      this.key = key;
    }
  }
}
