package com.zarbosoft.rendaw.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ROTuple {
  private final List data;

  public ROTuple(List data) {
    this.data = data;
  }

  public static ROTuple create(Object... data) {
    return new ROTuple(Arrays.asList(data));
  }

  public ROTuple append(ROTuple other) {
    List out = new ArrayList(data);
    out.addAll(other.data);
    return new ROTuple(out);
  }

  public Object get(int i) {
    return data.get(i);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ROTuple roTuple = (ROTuple) o;
    return data.equals(roTuple.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data);
  }

  public int size() {
    return data.size();
  }
}
