package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.Builtin;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;

/** Mixin to automatically semiserialize value based on (single) constructor arguments. */
public interface AutoBuiltinExportable extends Exportable {
  @Override
  default Exportable type() {
    return Builtin.autoBuiltinExportTypes.get(getClass());
  }

  @Override
  default void postInit() {}
}
