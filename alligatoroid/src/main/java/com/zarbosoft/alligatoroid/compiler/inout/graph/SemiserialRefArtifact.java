package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;

public class SemiserialRefArtifact implements SemiserialRef {
  @Exportable.Param public ArtifactId id;

  public static SemiserialRefArtifact create(ArtifactId id) {
    final SemiserialRefArtifact out = new SemiserialRefArtifact();
    out.id = id;
    return out;
  }

  @Override
  public <T> T dispatchRef(Dispatcher<T> dispatcher) {
    return dispatcher.handleArtifact(this);
  }
}
