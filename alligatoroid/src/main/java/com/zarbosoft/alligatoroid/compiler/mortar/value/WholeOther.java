package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeDumpable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.luxem.write.Writer;

public class WholeOther
    implements SimpleValue, TreeDumpable, AutoBuiltinExportable, LeafExportable {
  public final Object object;

  public WholeOther(Object object) {
    this.object = object;
  }

  @Override
  public void treeDump(Writer writer) {
    writer.type("other").primitive(object.toString());
  }
}
