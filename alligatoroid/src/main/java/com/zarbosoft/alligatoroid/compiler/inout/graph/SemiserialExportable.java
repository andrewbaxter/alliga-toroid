package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialExportable {
  @BuiltinAutoExporter.Param public SemiserialRef type;
  @BuiltinAutoExporter.Param public SemiserialSubvalue value;

  public static SemiserialExportable create(SemiserialRef type, SemiserialSubvalue data) {
    final SemiserialExportable out = new SemiserialExportable();
    out.type = type;
    out.value = data;
    return out;
  }
}
