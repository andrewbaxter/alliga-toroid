package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;

public final class ArtifactId {
  public final ImportId spec;
  public final int index;

  public ArtifactId(ImportId spec, int index) {
    this.spec = spec;
    this.index = index;
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
