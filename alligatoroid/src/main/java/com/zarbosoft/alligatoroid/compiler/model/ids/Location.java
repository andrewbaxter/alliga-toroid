package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.luxem.write.Writer;

public final class Location implements AutoBuiltinExportable, TreeDumpable {
  @AutoBuiltinExportableType.Param
  public int id;
  @AutoBuiltinExportableType.Param
  public ModuleId module;

  public static Location create(ModuleId module, int id) {
    final Location out = new Location();
    out.module = module;
    out.id = id;
    out.postInit();
    return out;
  }

  @Override
  public void treeDump(Writer writer) {
    writer.recordBegin().primitive("module");
    module.treeDump(writer);
    writer.primitive("id").primitive(Integer.toString(id)).recordEnd();
  }
}
