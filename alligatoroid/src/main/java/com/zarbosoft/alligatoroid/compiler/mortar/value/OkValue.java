package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.BindNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.CallNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public interface OkValue extends Value {
  @Override
  public default EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    context.moduleContext.errors.add(new CallNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public default EvaluateResult access(EvaluationContext context, Location location, Value field) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public default ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    context.moduleContext.errors.add(new BindNotSupported(location));
    return new ROPair<>(null, null);
  }

  @Override
  default Location location() {
    return null;
  }
}
