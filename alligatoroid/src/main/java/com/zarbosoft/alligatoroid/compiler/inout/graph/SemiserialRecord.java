package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROOrderedMap;
import com.zarbosoft.rendaw.common.ROPair;

public class SemiserialRecord implements SemiserialSubvalue {
  public final ROOrderedMap<SemiserialSubvalue, SemiserialSubvalue> data;

  public SemiserialRecord(ROOrderedMap<SemiserialSubvalue, SemiserialSubvalue> data) {
    this.data = data;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleRecord(this);
  }
}
