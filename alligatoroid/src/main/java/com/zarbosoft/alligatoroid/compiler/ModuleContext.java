package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.mortar.FutureValue;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class ModuleContext {
  /** Only in mortar. */
  public final ModuleId id;
  /** Only in mortar. */
  public final CompilationContext compilationContext;

  public final Log log = new Log();
  public final TSList<Location> sourceMapReverse = new TSList<>();
  public final TSMap<Location, Integer> sourceMapForward = new TSMap<>();

  public ModuleContext(ModuleId id, CompilationContext compilationContext) {
    this.id = id;
    this.compilationContext = compilationContext;
  }

  public final void builtinLog(String message) {
    log.log.add(message);
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
