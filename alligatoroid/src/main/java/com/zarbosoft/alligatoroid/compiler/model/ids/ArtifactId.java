package com.zarbosoft.alligatoroid.compiler.model.ids;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.luxem.write.Writer;

public final class ArtifactId implements TreeSerializable {
  public final ImportId spec;
  public final int index;

  public ArtifactId(ImportId spec, int index) {
    this.spec = spec;
    this.index = index;
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.recordBegin();
    writer.primitive("spec");
    spec.treeSerialize(writer);
    writer.primitive("index").primitive(Integer.toString(index));
    writer.recordEnd();
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
