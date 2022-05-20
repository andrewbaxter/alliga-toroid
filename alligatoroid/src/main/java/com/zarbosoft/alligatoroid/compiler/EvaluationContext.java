package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.TSList;

public class EvaluationContext {
  public final boolean isModuleRoot;
  public final ModuleCompileContext moduleContext;
  public final TargetModuleContext target;
  public final TSList<Location> sourceMapReverse = new TSList<>();
  public final TSList<String> log = new TSList<>();
  public final TSList<Error> errors = new TSList<>();
  public Scope scope;

  public EvaluationContext(
      ModuleCompileContext moduleContext,
      TargetModuleContext target,
      boolean isModuleRoot,
      Scope scope) {
    this.moduleContext = moduleContext;
    this.target = target;
    this.isModuleRoot = isModuleRoot;
    this.scope = scope;
  }

  public void pushScope() {
    this.scope = new Scope(scope);
  }

  public void popScope() {
    this.scope = scope.parent;
  }

  public int sourceLocation(Location location) {
    int out = sourceMapReverse.size();
    sourceMapReverse.add(location);
    return out;
  }
}
