package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.luxem.write.Writer;

public class SemiserialType implements SemiserialSubvalue {
  public static final String SERIAL_TYPE = "type";
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

  @Override
  public void treeSerialize(Writer writer) {
    writer.type(SERIAL_TYPE).recordBegin();
    writer.primitive("type").primitive(type);
    writer.primitive("value");
    value.treeSerialize(writer);
    writer.recordEnd();
  }
}
