package com.zarbosoft.alligatoroid.compiler.inout.graph;

import java.util.Objects;

public final class SemiserialInt implements SemiserialSubvalue {
  @BuiltinAutoExporter.Param
  public int value;

  public static SemiserialInt create(int value) {
    final SemiserialInt out = new SemiserialInt();
    out.value = value;
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleInt(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    SemiserialInt that = (SemiserialInt) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
