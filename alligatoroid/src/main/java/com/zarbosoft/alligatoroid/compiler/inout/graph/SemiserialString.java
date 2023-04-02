package com.zarbosoft.alligatoroid.compiler.inout.graph;

import java.util.Objects;

public final class SemiserialString implements SemiserialSubvalue {
  @BuiltinAutoExportableType.Param
  public String value;

  public static SemiserialString create(String value) {
    final SemiserialString out = new SemiserialString();
    out.value = value;
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleString(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    SemiserialString that = (SemiserialString) o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
