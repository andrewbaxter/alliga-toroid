package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.luxem.write.Writer;

public final class LocalModuleId implements ModuleId {
  public final String path;

  public LocalModuleId(String path) {
    this.path = path;
  }

  @Override
  public String toString() {
    return path.toString();
  }

  @Override
  public void serialize(Writer writer) {
    writer.type("local").recordBegin().key("path").primitive(path.toString()).recordEnd();
  }

  @Override
  public String hash() {
    return new Utils.SHA256().add(path.toString()).buildHex();
  }

  @Override
  public boolean equal1(ModuleId other) {
    return other.getClass() == this.getClass() && ((LocalModuleId) other).path.equals(path);
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handle(this);
  }
}
