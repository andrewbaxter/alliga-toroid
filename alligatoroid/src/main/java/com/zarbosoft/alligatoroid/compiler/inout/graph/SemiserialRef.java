package com.zarbosoft.alligatoroid.compiler.inout.graph;

public interface SemiserialRef extends SemiserialSubvalue {
  public <T> T dispatchRef(Dispatcher<T> dispatcher);

  @Override
  public default <T> T dispatch(SemiserialSubvalue.Dispatcher<T> dispatcher) {
    return dispatcher.handleRef(this);
  }

  public interface Dispatcher<T> {
    T handleArtifact(SemiserialRefArtifact s);

    T handleBuiltin(SemiserialRefBuiltin s);
  }
}