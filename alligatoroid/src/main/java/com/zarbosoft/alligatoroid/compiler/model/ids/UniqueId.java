package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.luxem.write.Writer;

/**
 * A unique id within an import/module.
 */
public class UniqueId implements TreeDumpable, AutoBuiltinExportable {
  @AutoBuiltinExportableType.Param
  public long importCacheId;
  @AutoBuiltinExportableType.Param
  public long subId;

  public UniqueId() {}

  public static UniqueId create(long importCacheId, int subId) {
    UniqueId out = new UniqueId();
    out.importCacheId = importCacheId;
    out.subId = subId;
    return out;
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
  public void treeDump(Writer writer) {
    writer.type("unique_id").recordBegin();
    writer.primitive("cache_id").primitive(Long.toString(importCacheId));
    writer.primitive("sub_id").primitive(Long.toString(subId));
    writer.recordEnd();
  }
}
