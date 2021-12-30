package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.luxem.write.Writer;

import java.util.Objects;

public final class SemiserialInt implements SemiserialSubvalue {
  public static final String SERIAL_TYPE = "int";
  public final int value;

  public SemiserialInt(int value) {
    this.value = value;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleInt(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type(SERIAL_TYPE).primitive(Integer.toString(value));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SemiserialInt that = (SemiserialInt) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
