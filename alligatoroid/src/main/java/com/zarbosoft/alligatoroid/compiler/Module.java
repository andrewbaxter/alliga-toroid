package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.concurrent.CompletableFuture;

/**
 * Module is everything related to the spec and result of compiling a module. The values here are
 * only used by the thread compiling the module or after all threads have been joined - no
 * cross-thread access or locking.
 */
public class Module {
  public final ImportSpec spec;
  public final CompilationContext compilationContext;
  public final CompletableFuture<Value> result;
  public final Log log = new Log();
  public final TSList<Location> sourceMapReverse = new TSList<>();
  public final TSList<CompletableFuture<Error>> deferredErrors = new TSList<>();
  public final TSMap<Location, Integer> sourceMapForward = new TSMap<>();
  public final ImportPath importPath;
  /** If present locally (ex: path in cache) */
  public String sourcePath;

  public Module(
      ImportSpec spec,
      ImportPath importPath,
      CompilationContext compilationContext,
      CompletableFuture<Value> result) {
    this.spec = spec;
    this.importPath = importPath;
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
