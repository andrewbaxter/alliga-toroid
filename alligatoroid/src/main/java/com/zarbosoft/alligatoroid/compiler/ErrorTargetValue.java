package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.ROPair;

public class ErrorTargetValue {
  public static final ErrorTargetValue targetValue = new ErrorTargetValue();

  private ErrorTargetValue() {}

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location location) {
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return EvaluateResult.error;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, ErrorValue.binding);
  }
}
