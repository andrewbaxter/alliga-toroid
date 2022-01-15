package com.zarbosoft.luxem.read.path;

import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public final class LuxemPath {
  /** First is index, second is key=true/value=false (always false except in records) */
  public final ROList<ROPair<Integer, Boolean>> data;

  public LuxemPath(ROList<ROPair<Integer, Boolean>> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("/");
    for (ROPair<Integer, Boolean> p : data) {
      if (builder.length() > 1) builder.append("/");
      builder.append(p.first);
      if (p.second) builder.append(" key");
    }
    return builder.toString();
  }
}
