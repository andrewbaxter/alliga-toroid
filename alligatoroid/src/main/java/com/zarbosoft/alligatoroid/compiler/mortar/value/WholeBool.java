package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.luxem.write.Writer;

import static com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateBool.BOOL_FALSE;
import static com.zarbosoft.alligatoroid.compiler.inout.utils.deserializer.DefaultStateBool.BOOL_TRUE;

public class WholeBool implements WholeValue {
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
  public void treeDump(Writer writer) {
    writer.type("bool").primitive(value ? BOOL_TRUE : BOOL_FALSE);
  }
}
