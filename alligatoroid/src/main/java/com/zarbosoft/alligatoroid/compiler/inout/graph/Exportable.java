package com.zarbosoft.alligatoroid.compiler.inout.graph;

import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.rendaw.common.ROList;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Exportable {
  /** Called after deferred initialization in graph desemiserialization. */
  default void postInit() {}

  ExportableType graphType();

  @Retention(RetentionPolicy.RUNTIME)
  @interface Param {}
}
