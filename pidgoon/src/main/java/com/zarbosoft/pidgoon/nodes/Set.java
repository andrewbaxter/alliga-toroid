package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.rendaw.common.TSList;

/** Match each child exactly once. */
public class Set<T> extends BaseSet<T, T> {
  @Override
  public TSList<T> combine(TSList<T> out, T value) {
    return out.add(value);
  }
}
