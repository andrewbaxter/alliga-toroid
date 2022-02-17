package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialValue {
  @Exportable.Param public SemiserialRef type;
  @Exportable.Param public SemiserialSubvalue data;

  public static SemiserialValue create(SemiserialRef type, SemiserialSubvalue data) {
    final SemiserialValue out = new SemiserialValue();
    out.type = type;
    out.data = data;
    return out;
  }
}
