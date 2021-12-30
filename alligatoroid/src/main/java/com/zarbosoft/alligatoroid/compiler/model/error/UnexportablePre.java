package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;

public class UnexportablePre extends Error.PreError {
  private final ROList<String> accessPath;

  public UnexportablePre(ROList<String> accessPath) {
    this.accessPath = accessPath;
  }

  @Override
  public Error toError(Location location) {
    return new Unexportable(location, accessPath);
  }
}
