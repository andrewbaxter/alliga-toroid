package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Artifact;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;

public interface NoExportValue extends Artifact {
  @Override
  default void postInit() {
    throw new Assertion();
  }

  @Override
  default SemiserialSubvalueExportable graphSemiserialize(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Artifact> path,
      ROList<String> accessPath) {
    throw new Assertion();
  }
}
