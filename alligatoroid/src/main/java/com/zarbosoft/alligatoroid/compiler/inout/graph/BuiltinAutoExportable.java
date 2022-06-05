package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.Meta;

/** Mixin to automatically semiserialize value based on (single) constructor arguments. */
public interface BuiltinAutoExportable extends Exportable {
  @Override
  default void postInit() {}

  @Override
  default ExportableType exportableType() {
    return Meta.autoExportableTypeLookup.get(getClass());
  }
}
