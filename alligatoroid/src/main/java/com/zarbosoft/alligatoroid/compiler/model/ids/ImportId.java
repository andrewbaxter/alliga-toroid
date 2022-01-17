package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.luxem.write.Writer;

public final class ImportId implements TreeDumpable {
  public final ModuleId moduleId;

  public ImportId(ModuleId moduleId) {
    this.moduleId = moduleId;
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
    // TODO encapsulate in another record when there are more fields
    moduleId.treeDump(writer);
  }

  public boolean equal1(ImportId other) {
    return moduleId.equal1(other.moduleId);
  }

  public String hash() {
    return moduleId.hash();
  }
}
