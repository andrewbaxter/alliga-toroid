package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialExportable {
  @BuiltinAutoExportableType.Param public SemiserialUnknown type;
  @BuiltinAutoExportableType.Param public SemiserialSubvalue value;

  public static SemiserialExportable create(SemiserialUnknown type, SemiserialSubvalue data) {
    final SemiserialExportable out = new SemiserialExportable();
    out.type = type;
    out.value = data;
    return out;
  }
}
