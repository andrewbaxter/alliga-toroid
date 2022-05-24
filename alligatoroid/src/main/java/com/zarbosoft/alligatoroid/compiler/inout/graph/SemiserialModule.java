package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROList;

public class SemiserialModule {
  @Artifact.Param public SemiserialSubvalueExportable root;
  @Artifact.Param public ROList<SemiserialExportableIdentityBody> artifacts;

  public static SemiserialModule create(
          SemiserialSubvalueExportable root, ROList<SemiserialExportableIdentityBody> artifacts) {
    final SemiserialModule out = new SemiserialModule();
    out.root = root;
    out.artifacts = artifacts;
    return out;
  }
}
