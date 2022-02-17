package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROList;

public class SemiserialModule {
  @Exportable.Param public SemiserialRef root;
  @Exportable.Param public ROList<SemiserialValue> artifacts;

  public static SemiserialModule create(SemiserialRef root, ROList<SemiserialValue> artifacts) {
    final SemiserialModule out = new SemiserialModule();
    out.root = root;
    out.artifacts = artifacts;
    return out;
  }
}
