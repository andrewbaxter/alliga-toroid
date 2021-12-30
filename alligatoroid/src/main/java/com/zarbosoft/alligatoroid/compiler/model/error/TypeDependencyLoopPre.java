package com.zarbosoft.alligatoroid.compiler.model.error;

public class TypeDependencyLoopPre extends Error.PreLocationlessError {
  @Override
  public Error toError() {
    return new TypeDependencyLoop();
  }
}
