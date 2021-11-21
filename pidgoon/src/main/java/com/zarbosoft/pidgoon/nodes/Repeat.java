package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.rendaw.common.TSList;

/** Match the child 0 or multiple times. */
public class Repeat<T> extends BaseRepeat<T, T> {
  public Repeat(Node<T> child) {
    super(child);
  }

  @Override
  public void combine(TSList<T> out, T value) {
    out.add(value);
  }
}
