package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROList;

public class SemiserialTuple implements SemiserialSubvalue {
  public final ROList<SemiserialSubvalue> values;

  public SemiserialTuple(ROList<SemiserialSubvalue> values) {
    this.values = values;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleTuple(this);
  }
}
