package com.zarbosoft.alligatoroid.compiler.inout.graph;

public interface Exportable extends Semiserializable {
  /** Called after deferred initialization in graph desemiserialization. */
  default void postInit() {}

  ExportableType exportableType();
}
