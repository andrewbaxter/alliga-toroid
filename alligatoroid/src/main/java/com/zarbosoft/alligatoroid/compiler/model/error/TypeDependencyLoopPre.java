package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

public class TypeDependencyLoopPre extends Error.PreError {
  private final ROList<String> exportPath;

  public TypeDependencyLoopPre(ROList<String> exportPath) {
    this.exportPath = exportPath;
  }

  @Override
  public String toString() {
    return Format.format(
        "An output of this module at %s has a dependency loop where type type of a value refers to a value of that type (directly or indirectly).",
        exportPath);
  }
}
