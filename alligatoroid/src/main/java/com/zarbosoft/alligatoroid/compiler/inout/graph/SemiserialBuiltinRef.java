package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialBuiltinRef implements SemiserialRef {
  @BuiltinAutoExportableType.Param
  public String key;

  public static SemiserialBuiltinRef create(String key) {
    final SemiserialBuiltinRef out = new SemiserialBuiltinRef();
    out.key = key;
    return out;
  }

  @Override
  public <T> T dispatchExportable(Dispatcher<T> dispatcher) {
    return dispatcher.handleBuiltinRef(this);
  }
}
