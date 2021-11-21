package com.zarbosoft.pidgoon.nodes;

import com.zarbosoft.rendaw.common.ROList;

public class HomogenousSequence<T> extends BaseSequence<T, T> {
  @Override
  protected ROList<T> collect(ROList<T> collection, T result) {
    return collection.mut().add(result);
  }
}
