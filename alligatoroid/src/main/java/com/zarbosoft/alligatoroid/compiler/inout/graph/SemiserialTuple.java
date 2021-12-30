package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROList;

public class SemiserialTuple implements SemiserialSubvalue {
  public static final String SERIAL_TYPE = "tuple";
  public final ROList<SemiserialSubvalue> values;

  public SemiserialTuple(ROList<SemiserialSubvalue> values) {
    this.values = values;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleTuple(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type(SERIAL_TYPE).arrayBegin();
    for (SemiserialSubvalue value : values) {
      value.treeSerialize(writer);
    }
    writer.arrayEnd();
  }
}
