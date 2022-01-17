package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialRefBuiltin implements SemiserialRef {
  public final String key;

  public SemiserialRefBuiltin(String key) {
    this.key = key;
  }

  @Override
  public <T> T dispatchRef(Dispatcher<T> dispatcher) {
    return dispatcher.handleBuiltin(this);
  }
}
