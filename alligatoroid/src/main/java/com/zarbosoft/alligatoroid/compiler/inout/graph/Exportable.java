package com.zarbosoft.alligatoroid.compiler.inout.graph;

public interface Exportable  {
  /** Called after deferred initialization in graph desemiserialization. */
  default void postInit() {}

  Exporter exporter();
}
