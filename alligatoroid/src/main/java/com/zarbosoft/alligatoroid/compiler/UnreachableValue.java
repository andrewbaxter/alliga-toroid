package com.zarbosoft.alligatoroid.compiler;

public class UnreachableValue implements Value {
  public static final UnreachableValue value = new UnreachableValue();

  private UnreachableValue() {}
}
