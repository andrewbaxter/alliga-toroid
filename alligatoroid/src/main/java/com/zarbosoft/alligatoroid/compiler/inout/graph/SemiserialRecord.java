package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROOrderedMap;

public class SemiserialRecord implements SemiserialSubvalue {
  @AutoExporter.Param
  public ROOrderedMap<SemiserialSubvalue, SemiserialSubvalue> data;

  public static SemiserialRecord create(ROOrderedMap<SemiserialSubvalue, SemiserialSubvalue> data) {
    final SemiserialRecord out = new SemiserialRecord();
    out.data = data;
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleRecord(this);
  }
}
