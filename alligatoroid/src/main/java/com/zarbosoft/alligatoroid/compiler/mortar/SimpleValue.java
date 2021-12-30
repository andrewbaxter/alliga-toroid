package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.OkValue;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.rendaw.common.ROPair;

public interface SimpleValue extends Binding, OkValue {
  @Override
  public default TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public default ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, this);
  }

  @Override
  public default EvaluateResult fork(EvaluationContext context, Location location) {
    return new EvaluateResult(null, null, this);
  }
}
