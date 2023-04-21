package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROList;

public class SemiserialTuple implements SemiserialSubvalue {
  @BuiltinAutoExporter.Param
  public ROList<SemiserialSubvalue> values;

  public static SemiserialTuple create(ROList<SemiserialSubvalue> values) {
    final SemiserialTuple out = new SemiserialTuple();
    out.values = values;
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleTuple(this);
  }
}
