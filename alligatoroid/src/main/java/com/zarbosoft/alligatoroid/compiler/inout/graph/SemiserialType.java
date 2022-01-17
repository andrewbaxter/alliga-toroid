package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialType implements SemiserialSubvalue {
  public final String type;
  public final SemiserialSubvalue value;

  public SemiserialType(String type, SemiserialSubvalue value) {
    this.type = type;
    this.value = value;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleType(this);
  }
}
