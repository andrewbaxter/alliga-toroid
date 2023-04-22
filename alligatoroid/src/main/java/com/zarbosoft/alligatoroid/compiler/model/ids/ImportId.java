package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;
import com.zarbosoft.luxem.write.Writer;

/**
 * A reference to an import that will deterministically produce an output. This means it includes
 * the override state.
 */
public final class ImportId implements TreeDumpable, AutoExportable {
  @AutoExporter.Param public ModuleId moduleId;

  public static ImportId create(ModuleId moduleId) {
    final ImportId importId = new ImportId();
    importId.moduleId = moduleId;
    importId.postInit();
    return importId;
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
