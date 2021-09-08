package com.zarbosoft.alligatoroid.compiler;

public final class ImportSpec {
  public final ModuleId moduleId;

  public ImportSpec(ModuleId moduleId) {
    this.moduleId = moduleId;
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
