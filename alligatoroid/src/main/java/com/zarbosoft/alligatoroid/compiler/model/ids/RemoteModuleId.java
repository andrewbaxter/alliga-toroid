package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportOutsideOwningBundleModule;
import com.zarbosoft.luxem.write.Writer;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class RemoteModuleId implements ModuleId, AutoExportable {
  public static final SemiserialString GRAPH_KEY_URL = SemiserialString.create("url");
  public static final SemiserialString GRAPH_KEY_HASH = SemiserialString.create("hash");
  @AutoExporter.Param public String url;
  @AutoExporter.Param public String hash;

  public static RemoteModuleId create(String url, String hash) {
    final RemoteModuleId out = new RemoteModuleId();
    out.url = url;
    out.hash = hash;
    out.postInit();
    return out;
  }

  public static RemoteModuleId graphDeserialize(SemiserialRecord data) {
    return RemoteModuleId.create(
        ((SemiserialString) data.data.get(GRAPH_KEY_URL)).value,
        ((SemiserialString) data.data.get(GRAPH_KEY_HASH)).value);
  }

  @Override
  public String toString() {
    return url;
  }

  @Override
  public ModuleId relative(String localPath) {
    Path subpath = Paths.get(localPath).normalize();
    if (subpath.startsWith("..")) {
      throw new ImportOutsideOwningBundleModule(subpath.toString(), this);
    }
    return BundleModuleSubId.create(this, subpath.toString());
  }

  @Override
  public void treeDump(Writer writer) {
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
