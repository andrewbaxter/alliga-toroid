package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialType implements SemiserialSubvalue {
  @Exportable.Param public String type;
  @Exportable.Param public SemiserialSubvalue value;

  public static SemiserialType create(String type, SemiserialSubvalue value) {
    final SemiserialType out = new SemiserialType();
    out.type = type;
    out.value = value;
    return out;
  }

  @Override
  public <T> T dispatch(Dispatcher<T> dispatcher) {
    return dispatcher.handleType(this);
  }
}
