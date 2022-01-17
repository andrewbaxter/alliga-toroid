package com.zarbosoft.luxem.read.path;

import com.zarbosoft.rendaw.common.ROList;

public final class LuxemPath {
  /** First is index, second is key=true/value=false (always false except in records) */
  public final ROList<Element> data;

  public LuxemPath(ROList<Element> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("/");
    for (Element p : data) {
      if (builder.length() > 1) builder.append("/");
      builder.append(p.index);
      if (p.key) builder.append(" key");
      for (int i = 0; i < p.typeCount; i += 1) {
        builder.append(" value");
      }
    }
    return builder.toString();
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
