package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.luxem.write.Writer;

public final class ImportSpec implements TreeSerializable {
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

  @Override
  public void serialize(Writer writer) {
    // TODO encapsulate in another record when there are more fields
    moduleId.serialize(writer);
  }
}
