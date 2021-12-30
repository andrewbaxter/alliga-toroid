package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.model.OkValue;
import com.zarbosoft.luxem.write.Writer;

public class WholeBool implements WholeValue, OkValue {
  public final boolean value;

  public WholeBool(boolean value) {
    this.value = value;
  }

  @Override
  public Object concreteValue() {
    return value;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleBool(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type("bool").primitive(value ? "true" : "false");
  }
}
