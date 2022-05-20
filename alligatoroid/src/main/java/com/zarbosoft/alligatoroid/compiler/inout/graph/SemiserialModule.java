package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.rendaw.common.ROList;

public class SemiserialModule {
  @AutoBuiltinExportableType.Param
  public SemiserialSubvalueRef root;
  @AutoBuiltinExportableType.Param
  public ROList<SemiserialExportableIdentityBody> artifacts;

  public static SemiserialModule create(
          SemiserialSubvalueRef root, ROList<SemiserialExportableIdentityBody> artifacts) {
    final SemiserialModule out = new SemiserialModule();
    out.root = root;
    out.artifacts = artifacts;
    return out;
  }
}
