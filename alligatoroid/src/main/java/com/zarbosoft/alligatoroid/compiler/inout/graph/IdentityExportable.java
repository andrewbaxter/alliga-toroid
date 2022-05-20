package com.zarbosoft.alligatoroid.compiler.inout.graph;

public interface IdentityExportable {
    /** Called after deferred initialization in graph desemiserialization. */
    default void postInit() {}
}
