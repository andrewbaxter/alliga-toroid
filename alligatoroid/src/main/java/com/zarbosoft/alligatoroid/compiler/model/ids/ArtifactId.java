package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExporter;

public final class ArtifactId {
  @AutoExporter.Param
  public long cacheId;
  @AutoExporter.Param
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
