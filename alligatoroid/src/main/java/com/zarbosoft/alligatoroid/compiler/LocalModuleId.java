package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.TSMap;

public final class LocalModuleId implements ModuleId {
  public static final String GRAPH_KEY_PATH = "path";
  public final String path;

  public LocalModuleId(String path) {
    this.path = path;
  }

  public static LocalModuleId graphDeserialize(Record data) {
    return new LocalModuleId((String) data.data.get(GRAPH_KEY_PATH));
  }

  @Override
  public String toString() {
    return path.toString();
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type("local").recordBegin().primitive("path").primitive(path.toString()).recordEnd();
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

  @Override
  public boolean equals(Object o) {
    return Utils.reflectEquals(this, o);
  }

  @Override
  public int hashCode() {
    return Utils.reflectHashCode(this);
  }

  @Override
  public Record graphSerialize() {
    return new Record(new TSMap<>().put(GRAPH_KEY_PATH, path));
  }
}
