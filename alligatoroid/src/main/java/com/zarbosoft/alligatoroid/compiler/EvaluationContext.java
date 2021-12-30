package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.concurrent.CompletableFuture;

public class EvaluationContext {
  public final ModuleCompileContext moduleContext;
  public final ImportId spec;
  public final TargetModuleContext target;
  public final TSList<ROPair<Location, CompletableFuture>> deferredErrors = new TSList<>();
  public final TSList<Location> sourceMapReverse = new TSList<>();
  public Scope scope;

  public EvaluationContext(
      ModuleCompileContext moduleContext,
      ImportId spec,
      TargetModuleContext target,
      Scope scope) {
    this.moduleContext = moduleContext;
    this.spec = spec;
    this.target = target;
    this.scope = scope;
  }

  public void pushScope() {
    this.scope = new Scope(scope);
  }

  public void popScope() {
    this.scope = scope.parent;
  }

  public int sourceLocation(Location location) {
  }
}
