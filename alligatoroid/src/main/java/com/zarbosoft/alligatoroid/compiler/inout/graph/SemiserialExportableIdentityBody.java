package com.zarbosoft.alligatoroid.compiler.inout.graph;

public class SemiserialExportableIdentityBody {
  @Artifact.Param public SemiserialSubvalueExportable type;
  @Artifact.Param public SemiserialSubvalue data;

  public static SemiserialExportableIdentityBody create(SemiserialSubvalueExportable type, SemiserialSubvalue data) {
    final SemiserialExportableIdentityBody out = new SemiserialExportableIdentityBody();
    out.type = type;
    out.data = data;
    return out;
  }
}
