package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

public class UnexportablePre extends Error.PreError {
  private final ROList<String> accessPath;

  public UnexportablePre(ROList<String> accessPath) {
    this.accessPath = accessPath;
  }

  @Override
  public String toString() {
    return Format.format("Object at [%s] cannot be exported", accessPath);
  }
}
