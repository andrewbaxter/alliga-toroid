package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.concurrent.CompletableFuture;

/** Module-wide context used when compiling. */
public class Module {
  public final ModuleId id;
  public final CompilationContext compilationContext;
  public final CompletableFuture<Value> result;
  public final Log log = new Log();
  public final TSList<Location> sourceMapReverse = new TSList<>();
  public final TSMap<Location, Integer> sourceMapForward = new TSMap<>();
  /** If present locally (ex: path in cache) */
  public String sourcePath;

  public Module(ModuleId id, CompilationContext compilationContext, CompletableFuture<Value> result) {
    this.id = id;
    this.compilationContext = compilationContext;
    this.result = result;
  }

  public int sourceLocation(Location location) {
    return sourceMapForward.getCreate(
        location,
        () -> {
          int out = sourceMapReverse.size();
          sourceMapReverse.add(location);
          return out;
        });
  }
}
