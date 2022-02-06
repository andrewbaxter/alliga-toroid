package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class SimpleBinding implements Binding {
  private final SimpleValue value;

  public SimpleBinding(SimpleValue value) {
    this.value = value;
  }

  @Override
  public EvaluateResult fork(EvaluationContext context, Location location) {
    return EvaluateResult.pure(value);
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return null;
  }
}
