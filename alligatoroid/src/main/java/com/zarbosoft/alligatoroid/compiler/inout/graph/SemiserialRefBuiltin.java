package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialRefBuiltin implements SemiserialRef {
  @Exportable.Param public String key;

  public static SemiserialRefBuiltin create(String key) {
    final SemiserialRefBuiltin out = new SemiserialRefBuiltin();
    out.key = key;
    return out;
  }

  @Override
  public <T> T dispatchRef(Dispatcher<T> dispatcher) {
    return dispatcher.handleBuiltin(this);
  }
}
