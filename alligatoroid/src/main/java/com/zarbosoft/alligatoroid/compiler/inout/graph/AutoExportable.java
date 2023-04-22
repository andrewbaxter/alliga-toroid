package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;

/**
 * Mixin to automatically semiserialize data. There are types that can use this: singletons, where
 * they'll skip the de/semiserialization (set up statically) and objects constructed by field access
 * with a postInit method (must be registered in static autogen).
 */
public interface AutoExportable extends Exportable {
  @Override
  default void postInit() {}

  @Override
  default Exporter exporter() {
    return StaticAutogen.autoExporterLookup.get(getClass());
  }
}
