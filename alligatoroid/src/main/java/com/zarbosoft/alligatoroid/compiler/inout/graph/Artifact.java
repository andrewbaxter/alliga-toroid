package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.rendaw.common.ROList;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Artifact {
  SemiserialSubvalueExportable graphSemiserialize(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Artifact> path,
      ROList<String> accessPath);

  @Retention(RetentionPolicy.RUNTIME)
  @interface Param {}
}
