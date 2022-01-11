package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.luxem.write.Writer;

public class WholeString implements WholeValue, OkValue, LeafValue, AutoGraphMixin {
  public final String value;

  public WholeString(String value) {
    this.value = value;
  }

  @Override
  public Object concreteValue() {
    return value;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleString(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type("string").primitive(value);
  }
}
