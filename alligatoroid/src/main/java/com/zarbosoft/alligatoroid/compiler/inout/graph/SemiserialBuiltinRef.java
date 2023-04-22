package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialBuiltinRef implements SemiserialRef {
  @AutoExporter.Param
  public int index;

  public static SemiserialBuiltinRef create(int index) {
    final SemiserialBuiltinRef out = new SemiserialBuiltinRef();
    out.index = index;
    return out;
  }

  @Override
  public <T> T dispatchExportable(Dispatcher<T> dispatcher) {
    return dispatcher.handleBuiltinRef(this);
  }
}
