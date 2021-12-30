package com.zarbosoft.alligatoroid.compiler.model.error;

public class TypeDependencyLoop extends Error.LocationlessError {
  @Override
  public String toString() {
    return "An output of this module has a dependency loop where type type of a value refers to a value of that type (directly or indirectly).";
  }
}
