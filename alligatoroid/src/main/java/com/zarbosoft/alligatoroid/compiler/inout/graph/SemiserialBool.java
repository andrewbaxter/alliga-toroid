package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.luxem.write.Writer;

import java.util.Objects;

public final class SemiserialBool implements SemiserialSubvalue {
  public static final String SERIAL_TYPE = "bool";
  public final boolean value;

  public SemiserialBool(boolean value) {
    this.value = value;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleBool(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type(SERIAL_TYPE).primitive(value ? "true" : "false");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SemiserialBool that = (SemiserialBool) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
