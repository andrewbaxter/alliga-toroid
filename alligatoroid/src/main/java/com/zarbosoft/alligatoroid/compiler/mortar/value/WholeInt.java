package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.luxem.write.Writer;

public class WholeInt implements WholeValue, OkValue, LeafValue, AutoGraphMixin {
  public final int value;

  public WholeInt(int value) {
    this.value = value;
  }

  @Override
  public Integer concreteValue() {
    return value;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleInt(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type("int").primitive(Integer.toString(value));
  }
}
