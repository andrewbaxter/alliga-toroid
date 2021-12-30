package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class ErrorBinding implements Binding {
  public static final ErrorBinding binding = new ErrorBinding();

  private ErrorBinding() {}

  @Override
  public EvaluateResult fork(EvaluationContext context, Location location) {
    return EvaluateResult.error;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }
}
