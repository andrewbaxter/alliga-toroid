package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Assertion;

/** Pseudo module-id for root of compilation (outside any file) */
public class RootModuleId implements ModuleId, AutoExportable {
  @Override
  public String toString() {
    return "root";
  }

  @Override
  public ModuleId relative(String localPath) {
    throw new Assertion();
  }

  @Override
  public void treeDump(Writer writer) {
    writer.type("root").recordBegin().recordEnd();
  }

  @Override
  public String hash() {
    return new Utils.SHA256().add("").buildHex();
  }

  @Override
  public boolean equal1(ModuleId other) {
    return other.getClass() == this.getClass();
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleRoot(this);
  }

  @Override
  public boolean equals(Object o) {
    return Utils.reflectEquals(this, o);
  }

  @Override
  public int hashCode() {
    return Utils.reflectHashCode(this);
  }
}
