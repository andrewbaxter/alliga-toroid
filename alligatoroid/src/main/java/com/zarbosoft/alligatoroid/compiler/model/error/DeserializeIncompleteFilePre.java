package com.zarbosoft.alligatoroid.compiler.model.error;

public class DeserializeIncompleteFilePre extends Error.PreError {
  public final String cachePath;

  public DeserializeIncompleteFilePre(String cachePath) {
    this.cachePath = cachePath;
  }

  @Override
  public String toString() {
    return "This source file ended before all expected data was read";
  }
}
