package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalueRef;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;

public interface NoExportValue {
  @Override
  default void postInit() {
    throw new Assertion();
  }

  @Override
  default SemiserialSubvalueRef graphSemiserialize(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    throw new Assertion();
  }
}
