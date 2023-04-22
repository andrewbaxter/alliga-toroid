package com.zarbosoft.alligatoroid.compiler.inout.graph;

import java.util.Objects;

public final class SemiserialBool implements SemiserialSubvalue {
  @AutoExporter.Param
  public boolean value;

  public static SemiserialBool create(boolean value) {
    final SemiserialBool out = new SemiserialBool();
    out.value = value;
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleBool(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    SemiserialBool that = (SemiserialBool) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
