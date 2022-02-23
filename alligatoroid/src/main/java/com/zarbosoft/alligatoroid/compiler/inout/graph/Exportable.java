package com.zarbosoft.alligatoroid.compiler.inout.graph;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Exportable {
  /** Called after deferred initialization in graph desemiserialization. */
  default void postInit() {}

  ExportableType graphType();

  @Retention(RetentionPolicy.RUNTIME)
  @interface Param {}
}
