package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.rendaw.common.ROList;

public class Discard<T> extends Operator<Object, ROList<T>> {
  public Discard(Node<?> root) {
    super((Node<Object>) root);
  }

  @Override
  protected ROList<T> process(Object value) {
    return ROList.empty;
  }
}
