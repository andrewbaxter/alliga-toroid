package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROList;

public class SemiserialModule {
  @BuiltinAutoExportableType.Param public SemiserialUnknown root;
  @BuiltinAutoExportableType.Param public ROList<SemiserialExportable> artifacts;

  public static SemiserialModule create(
      SemiserialUnknown root, ROList<SemiserialExportable> artifacts) {
    final SemiserialModule out = new SemiserialModule();
    out.root = root;
    out.artifacts = artifacts;
    return out;
  }
}
