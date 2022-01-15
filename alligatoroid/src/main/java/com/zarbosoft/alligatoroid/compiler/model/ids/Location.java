package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.LeafExportable;
import com.zarbosoft.luxem.write.Writer;

public final class Location implements TreeSerializable, AutoBuiltinExportable, LeafExportable {
  public final ModuleId module;
  public final int id;

  public Location(ModuleId module, int id) {
    this.module = module;
    this.id = id;
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.recordBegin().primitive("module");
    module.treeSerialize(writer);
    writer.primitive("id").primitive(Integer.toString(id)).recordEnd();
  }
}
