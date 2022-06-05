package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;

public class SemiserialExportableRef implements SemiserialUnknown {
  @BuiltinAutoExportableType.Param
  public ArtifactId id;

  public static SemiserialExportableRef create(ArtifactId id) {
    final SemiserialExportableRef out = new SemiserialExportableRef();
    out.id = id;
    return out;
  }

  @Override
  public <T> T dispatchExportable(Dispatcher<T> dispatcher) {
    return dispatcher.handleExportableRef(this);
  }
}
