package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;
import com.zarbosoft.luxem.write.Writer;

public final class Location implements AutoExportable, TreeDumpable {
  public final static Location rootLocation = null;
    @AutoExporter.Param
  public int id;
  @AutoExporter.Param
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
