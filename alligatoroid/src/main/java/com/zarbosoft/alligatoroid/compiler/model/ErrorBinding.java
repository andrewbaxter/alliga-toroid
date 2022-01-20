package com.zarbosoft.alligatoroid.compiler.model;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;

public class ErrorBinding implements MortarBinding {
  public static final ErrorBinding binding = new ErrorBinding();

  private ErrorBinding() {}

  @Override
  public EvaluateResult mortarFork(EvaluationContext context, Location location) {
    throw new Assertion();
  }

  @Override
  public TargetCode mortarDrop(EvaluationContext context, Location location) {
    throw new Assertion();
  }
}
