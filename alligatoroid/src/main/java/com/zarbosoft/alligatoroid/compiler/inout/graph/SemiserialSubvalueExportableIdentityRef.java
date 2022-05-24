package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;

public class SemiserialSubvalueExportableIdentityRef implements SemiserialSubvalueExportable {
  @Artifact.Param public ArtifactId id;

  public static SemiserialSubvalueExportableIdentityRef create(ArtifactId id) {
    final SemiserialSubvalueExportableIdentityRef out = new SemiserialSubvalueExportableIdentityRef();
    out.id = id;
    return out;
  }

  @Override
  public <T> T dispatchExportable(Dispatcher<T> dispatcher) {
    return dispatcher.handleArtifact(this);
  }
}
