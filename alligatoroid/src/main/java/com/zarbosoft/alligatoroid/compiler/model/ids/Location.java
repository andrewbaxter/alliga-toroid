package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.luxem.write.Writer;

public final class Location implements AutoBuiltinExportable, LeafExportable, TreeDumpable {
  public final int id;
  public ModuleId module;

  public Location(ModuleId module, int id) {
    this.module = module;
    this.id = id;
  }

  @Override
  public void treeDump(Writer writer) {
    writer.recordBegin().primitive("module");
    module.treeDump(writer);
    writer.primitive("id").primitive(Integer.toString(id)).recordEnd();
  }
}
