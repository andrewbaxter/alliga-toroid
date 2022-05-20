package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;

public class SemiserialExportableIdentityBody {
  @AutoBuiltinExportableType.Param
  public SemiserialSubvalue type;
  @AutoBuiltinExportableType.Param
  public SemiserialSubvalue data;

  public static SemiserialExportableIdentityBody create(SemiserialSubvalue type, SemiserialSubvalue data) {
    final SemiserialExportableIdentityBody out = new SemiserialExportableIdentityBody();
    out.type = type;
    out.data = data;
    return out;
  }
}
