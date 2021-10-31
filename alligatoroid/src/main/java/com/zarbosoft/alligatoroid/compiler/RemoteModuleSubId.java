package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.TSMap;

public final class RemoteModuleSubId implements ModuleId {
  public static final String GRAPH_KEY_MODULE = "module";
  public static final String GRAPH_KEY_PATH = "path";
  public final RemoteModuleId module;
  public final String path;

  public RemoteModuleSubId(RemoteModuleId module, String path) {
    this.module = module;
    this.path = path;
  }

  public static RemoteModuleSubId graphDeserialize(Record data) {
    return new RemoteModuleSubId(
        (RemoteModuleId) data.data.get(GRAPH_KEY_MODULE), (String) data.data.get(GRAPH_KEY_PATH));
  }

  @Override
  public String toString() {
    return Format.format("%s %s", module, path);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type("local").recordBegin().primitive("module");
    module.treeSerialize(writer);
    writer.primitive("path").primitive(path).recordEnd();
  }

  @Override
  public String hash() {
    return new Utils.SHA256().add(module.hash()).add(path).buildHex();
  }

  @Override
  public boolean equal1(ModuleId other) {
    return other.getClass() == this.getClass()
        && ((RemoteModuleSubId) other).module.equal1(module)
        && ((RemoteModuleSubId) other).path.equals(path);
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
    return new Record(new TSMap<>().put(GRAPH_KEY_MODULE, module).put(GRAPH_KEY_PATH, path));
  }
}
