package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.TSList;

public class EvaluationContext {
  public final boolean isModuleRoot;
  public final ModuleCompileContext moduleContext;
  public final TargetModuleContext target;
  // Line number -> AT location
  public final TSList<Location> sourceMapReverse;
  public final TSList<String> log;
  public final TSList<Error> errors;
  public Scope scope;

  private EvaluationContext(
          ModuleCompileContext moduleContext,
          TargetModuleContext target,
          boolean isModuleRoot,
          TSList<Location> sourceMapReverse, TSList<String> log, TSList<Error> errors, Scope scope) {
    this.moduleContext = moduleContext;
    this.target = target;
    this.isModuleRoot = isModuleRoot;
    this.sourceMapReverse = sourceMapReverse;
    this.log = log;
    this.errors = errors;
    this.scope = scope;
  }

  public static EvaluationContext create(
          ModuleCompileContext moduleContext,
          TargetModuleContext target,
          boolean isModuleRoot
  ) {
    return new EvaluationContext(moduleContext, target, isModuleRoot, new TSList<>(), new TSList<>(), new TSList<>(), Scope.create());
  }

  public void pushScope() {
    this.scope = Scope.createChild(scope);
  }

  public void popScope() {
    this.scope = scope.parent;
  }

  public EvaluationContext forkScope() {
    return new EvaluationContext(moduleContext,target,isModuleRoot,sourceMapReverse,log,errors,scope.fork());
  }

  public int sourceLocation(Location location) {
    int out = sourceMapReverse.size();
    sourceMapReverse.add(location);
    return out;
  }
}
