package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.Format;

public final class BundleModuleSubId implements ModuleId, AutoBuiltinExportable, LeafExportable {
  public static final String GRAPH_KEY_MODULE = "module";
  public static final String GRAPH_KEY_PATH = "path";
  public final String path;
  public ModuleId module;

  public BundleModuleSubId(ModuleId module, String path) {
    this.module = module;
    this.path = path;
  }

  @Override
  public String toString() {
    return Format.format("%s %s", module, path);
  }

  @Override
  public void treeDump(Writer writer) {
    writer.type("local").recordBegin().primitive("module");
    module.treeDump(writer);
    writer.primitive("path").primitive(path).recordEnd();
  }

  @Override
  public String hash() {
    return new Utils.SHA256().add(module.hash()).add(path).buildHex();
  }

  @Override
  public boolean equal1(ModuleId other) {
    return other.getClass() == this.getClass()
        && ((BundleModuleSubId) other).module.equal1(module)
        && ((BundleModuleSubId) other).path.equals(path);
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleBundle(this);
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
