package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.rendaw.common.ROPair;

public interface OkValue extends Value {
  @Override
  public default EvaluateResult call(Context context, Location location, Value argument) {
    context.module.log.errors.add(Error.callNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public default EvaluateResult access(Context context, Location location, Value field) {
    context.module.log.errors.add(Error.accessNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public default ROPair<TargetCode, Binding> bind(Context context, Location location) {
    context.module.log.errors.add(Error.bindNotSupported(location));
    return new ROPair<>(null, null);
  }

  @Override
  default Location location() {
    return null;
  }
}
