package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROList;

public class SemiserialModule implements TreeSerializable {
  public static final String KEY_ROOT = "root";
  public static final String KEY_ARTIFACTS = "artifacts";
  public final SemiserialRef root;
  public final ROList<SemiserialValue> artifacts;

  public SemiserialModule(SemiserialRef root, ROList<SemiserialValue> artifacts) {
    this.root = root;
    this.artifacts = artifacts;
  }

  @Override
  public void treeSerialize(Writer writer) {
    writer.recordBegin();
    writer.primitive(KEY_ROOT);
    root.treeSerialize(writer);
    writer.primitive(KEY_ARTIFACTS).arrayBegin();
    for (SemiserialValue artifact : artifacts) {
      artifact.treeSerialize(writer);
    }
    writer.arrayEnd();
    writer.recordEnd();
  }
}
