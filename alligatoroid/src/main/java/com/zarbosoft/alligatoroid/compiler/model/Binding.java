package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface Binding {
  /**
   * Forks a bound value (part remains bound, returned value is temporary/on-stack)
   *
   * @param context
   * @param location
   * @return
   */
  EvaluateResult fork(EvaluationContext context, Location location);

  TargetCode drop(EvaluationContext context, Location location);
}
