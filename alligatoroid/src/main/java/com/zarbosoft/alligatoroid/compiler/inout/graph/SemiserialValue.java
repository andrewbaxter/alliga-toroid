package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialValue {
  public final SemiserialRef type;
  public final SemiserialSubvalue data;

  public SemiserialValue(SemiserialRef type, SemiserialSubvalue data) {
    this.type = type;
    this.data = data;
  }
}
