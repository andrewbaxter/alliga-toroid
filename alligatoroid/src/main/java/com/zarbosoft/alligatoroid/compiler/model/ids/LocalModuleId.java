package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.luxem.write.Writer;

public final class LocalModuleId implements ModuleId, AutoBuiltinExportable, Exportable {
  public static final String GRAPH_KEY_PATH = "path";
  @Param public String path;

  public static LocalModuleId graphDeserialize(Record data) {
    return create((String) data.data.get(GRAPH_KEY_PATH));
  }

  /** @param path Absolute path */
  public static LocalModuleId create(String path) {
    final LocalModuleId out = new LocalModuleId();
    out.path = path;
    out.postInit();
    return out;
  }

  @Override
  public String toString() {
    return path.toString();
  }

  @Override
  public void treeDump(Writer writer) {
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
    return dispatcher.handleLocal(this);
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
