package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;

public final class ArtifactId {
  @BuiltinAutoExportableType.Param
  public long cacheId;
  @BuiltinAutoExportableType.Param
  public int index;

  public ArtifactId() {}

  public static ArtifactId create(long cacheId, int index) {
    ArtifactId out = new ArtifactId();
    out.cacheId = cacheId;
    out.index = index;
    return out;
  }

  @Override
  public boolean equals(Object o) {
    return Utils.reflectEquals(this, o);
  }

  @Override
  public int hashCode() {
    return Utils.reflectHashCode(this);
  }
}
