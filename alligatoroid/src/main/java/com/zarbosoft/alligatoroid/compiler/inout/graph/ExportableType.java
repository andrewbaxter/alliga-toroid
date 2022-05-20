package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROList;

public interface ExportableType {
  SemiserialSubvalue graphSemiserializeValue(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Object> path,
      ROList<String> accessPath,
      Object value);
}
