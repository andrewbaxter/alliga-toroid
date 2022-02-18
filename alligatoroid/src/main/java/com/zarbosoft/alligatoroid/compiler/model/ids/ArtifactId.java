package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;

public final class ArtifactId {
  @Exportable.Param public ImportId spec;
  @Exportable.Param public int index;

  public ArtifactId() {}

  public static ArtifactId create(ImportId spec, int index) {
    ArtifactId out = new ArtifactId();
    out.spec = spec;
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
