package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;

public class TypeDependencyLoopPre extends Error.PreError {
  private final ROList<String> accessPath;

  public TypeDependencyLoopPre(ROList<String> accessPath) {
    this.accessPath = accessPath;
  }

  @Override
  public Error toError(Location location) {
    return new TypeDependencyLoop(location, accessPath);
  }
}
