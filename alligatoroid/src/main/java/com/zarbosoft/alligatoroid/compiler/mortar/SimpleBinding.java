package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

/** For special constant values only (bindings, future values) */
public class SimpleBinding implements Binding {
  private final Value value;

  public SimpleBinding(Value value) {
    this.value = value;
  }

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(value);
  }

  @Override
  public Binding fork() {
    return this;
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public boolean merge(
      EvaluationContext context, Location location, Binding other, Location otherLocation) {
    return true;
  }
}
