package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.model.ids.ArtifactId;
import com.zarbosoft.luxem.write.Writer;

public class SemiserialRefArtifact implements SemiserialRef {
  public static final String SERIAL_TYPE = "ref_artifact";
  public final ArtifactId id;

  public SemiserialRefArtifact(ArtifactId id) {
    this.id = id;
  }

  @Override
  public <T> T dispatchRef(Dispatcher<T> dispatcher) {
    return dispatcher.handleArtifact(this);
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.type(SERIAL_TYPE);
    id.treeSerialize(writer);
  }
}
