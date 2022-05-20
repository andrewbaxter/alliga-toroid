package com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto;

import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportable;

/** Mixin to automatically semiserialize value based on (single) constructor arguments. */
public interface AutoBuiltinExportable extends IdentityExportable {
  @Override
  default void postInit() {}
}
