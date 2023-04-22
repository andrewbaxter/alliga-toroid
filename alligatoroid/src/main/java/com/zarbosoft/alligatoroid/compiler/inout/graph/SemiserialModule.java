package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROList;

public class SemiserialModule {
  @AutoExporter.Param public SemiserialRef rootType;
  @AutoExporter.Param public SemiserialRef root;
  @AutoExporter.Param public ROList<SemiserialExportable> artifacts;

  public static SemiserialModule create(
      SemiserialRef rootType, SemiserialRef root, ROList<SemiserialExportable> artifacts) {
    final SemiserialModule out = new SemiserialModule();
    out.rootType = rootType;
    out.root = root;
    out.artifacts = artifacts;
    return out;
  }
}
