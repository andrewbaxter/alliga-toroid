package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.rendaw.common.ROPair;

public interface OkValue extends Value {
  @Override
  public default EvaluateResult call(Context context, Location location, Value argument) {
    context.module.log.errors.add(new Error.CallNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public default EvaluateResult access(Context context, Location location, Value field) {
    context.module.log.errors.add(new Error.AccessNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public default ROPair<TargetCode, Binding> bind(Context context, Location location) {
    context.module.log.errors.add(new Error.BindNotSupported(location));
    return new ROPair<>(null, null);
  }

  @Override
  default Location location() {
    return null;
  }
}
