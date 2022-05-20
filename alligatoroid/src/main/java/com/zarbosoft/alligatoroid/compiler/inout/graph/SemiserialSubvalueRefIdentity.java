package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;
import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;

public class SemiserialSubvalueRefIdentity implements SemiserialSubvalueRef {
  @AutoBuiltinExportableType.Param
  public ArtifactId id;

  public static SemiserialSubvalueRefIdentity create(ArtifactId id) {
    final SemiserialSubvalueRefIdentity out = new SemiserialSubvalueRefIdentity();
    out.id = id;
    return out;
  }

  @Override
  public <T> T dispatchExportable(Dispatcher<T> dispatcher) {
    return dispatcher.handleArtifact(this);
  }
}
