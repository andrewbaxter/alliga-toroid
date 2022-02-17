package com.zarbosoft.alligatoroid.compiler.mortar.graph;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;

/** A single instance exists, exportable looks it up by class name turned into a key. */
public interface SingletonBuiltinExportable extends Exportable {
  @Override
  default ExportableType graphType() {
    return BuiltinSingletonExportType.exportType;
  }
}
