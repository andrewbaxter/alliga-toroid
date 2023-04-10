package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface Binding {
  /**
   * Forks a bound value (part remains bound, returned value is temporary/on-stack)
   *
   * @param context
   * @param location
   * @return
   */
  EvaluateResult load(EvaluationContext context, Location location);

  Binding fork();

  TargetCode dropCode(EvaluationContext context, Location location);

  boolean merge(EvaluationContext context, Location location, Binding other);
}
