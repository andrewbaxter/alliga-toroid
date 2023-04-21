package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROList;

public class SemiserialModule {
  @BuiltinAutoExporter.Param public SemiserialRef root;
  @BuiltinAutoExporter.Param public ROList<SemiserialExportable> artifacts;

  public static SemiserialModule create(
          SemiserialRef root, ROList<SemiserialExportable> artifacts) {
    final SemiserialModule out = new SemiserialModule();
    out.root = root;
    out.artifacts = artifacts;
    return out;
  }
}
