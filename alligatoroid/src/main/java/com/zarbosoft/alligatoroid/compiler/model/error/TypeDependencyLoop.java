package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

public class TypeDependencyLoop extends Error.LocationError {
  private final ROList<String> exportPath;

  public TypeDependencyLoop(Location location, ROList<String> exportPath) {
    super(location);
    this.exportPath = exportPath;
  }

  @Override
  public String toString() {
    return Format.format(
        "An output of this module at %s has a dependency loop where type type of a value refers to a value of that type (directly or indirectly).",
        exportPath);
  }
}
