package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.luxem.write.Writer;

public final class RemoteModuleId implements ModuleId {
  public static final String GRAPH_KEY_URL = "url";
  public static final String GRAPH_KEY_HASH = "hash";
  public final String url;
  public final String hash;

  public RemoteModuleId(String url, String hash) {
    this.url = url;
    this.hash = hash;
  }

  public static RemoteModuleId graphDeserialize(Record data) {
    return new RemoteModuleId(
        (String) data.data.get(GRAPH_KEY_URL), (String) data.data.get(GRAPH_KEY_HASH));
  }

  @Override
  public String toString() {
    return url;
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer
        .type("local")
        .recordBegin()
        .primitive("url")
        .primitive(url)
        .primitive("hash")
        .primitive(hash)
        .recordEnd();
  }

  @Override
  public String hash() {
    return new Utils.SHA256().add(url).add(hash).buildHex();
  }

  @Override
  public boolean equal1(ModuleId other) {
    return other.getClass() == this.getClass()
        && ((RemoteModuleId) other).url.equals(url)
        && ((RemoteModuleId) other).hash.equals(hash);
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleRemote(this);
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
