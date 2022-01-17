package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.inout.tree.TreeSerializable;
import com.zarbosoft.luxem.write.Writer;
import com.zarbosoft.rendaw.common.ROList;

public class SemiserialModule  {
  public final SemiserialRef root;
  public final ROList<SemiserialValue> artifacts;

  public SemiserialModule(SemiserialRef root, ROList<SemiserialValue> artifacts) {
    this.root = root;
    this.artifacts = artifacts;
  }
}
