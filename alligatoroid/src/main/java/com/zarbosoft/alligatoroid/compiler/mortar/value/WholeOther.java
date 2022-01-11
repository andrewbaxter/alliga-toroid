package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.luxem.write.Writer;

public class WholeOther implements WholeValue, AutoGraphMixin, LeafValue {
  public final Object object;

  public WholeOther(Object object) {
    this.object = object;
  }

  @Override
  public Object concreteValue() {
    return object;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleOther(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type("other").primitive(object.toString());
  }
}
