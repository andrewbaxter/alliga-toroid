package com.zarbosoft.alligatoroid.compiler.inout.graph;

/** A single instance exists, exportable looks it up by class name turned into a key. */
public interface BuiltinSingletonExportable extends Exportable {
  @Override
  default ExportableType exportableType() {
    return BuiltinSingletonExportableType.exportableType;
  }
}
