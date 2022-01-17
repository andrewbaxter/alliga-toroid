package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.luxem.write.Writer;

public class WholeOther implements WholeValue, AutoBuiltinExportable, Exportable {
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
  public void treeDump(Writer writer) {
    writer.type("other").primitive(object.toString());
  }
}
