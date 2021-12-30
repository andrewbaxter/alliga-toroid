package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.luxem.write.Writer;

public class SemiserialRefBuiltin implements SemiserialRef {
  public static final String SERIAL_TYPE = "ref_builtin";
  public final String key;

  public SemiserialRefBuiltin(String key) {
    this.key = key;
  }

  @Override
  public <T> T dispatchRef(Dispatcher<T> dispatcher) {
    return dispatcher.handleBuiltin(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type(SERIAL_TYPE).primitive(key);
  }
}
