package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface MortarBinding extends Binding {
  /**
   * Forks a bound value (part remains bound, returned value is temporary/on-stack)
   *
   * @param context
   * @param location
   * @return
   */
  EvaluateResult mortarFork(EvaluationContext context, Location location);

  TargetCode mortarDrop(EvaluationContext context, Location location);
}
