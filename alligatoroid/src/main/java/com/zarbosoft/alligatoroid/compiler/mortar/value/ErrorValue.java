package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public final class ErrorValue implements Value {
  public static final ErrorValue error = new ErrorValue();
  public static final Binding binding =
      new Binding() {
        @Override
        public EvaluateResult fork(EvaluationContext context, Location location) {
          return EvaluateResult.error;
        }

        @Override
        public TargetCode dropCode(EvaluationContext context, Location location) {
          return null;
        }
      };

  private ErrorValue() {}

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
    return new ROPair<>(null, binding);
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location location) {
    return EvaluateResult.error;
  }

  @Override
  public Location location() {
    return null;
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    return EvaluateResult.error;
  }
}
