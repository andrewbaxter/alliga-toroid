package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class NullBinding implements Binding {
  public static final NullBinding binding = new NullBinding();

  private NullBinding() {}

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(NullValue.value);
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return null;
  }
}
