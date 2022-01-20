package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.model.MortarBinding;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.rendaw.common.ROPair;

public interface SimpleValue extends MortarBinding, OkValue {
  @Override
  public default TargetCode mortarDrop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public default ROPair<TargetCode, MortarBinding> mortarBind(EvaluationContext context, Location location) {
    return new ROPair<>(null, this);
  }

  @Override
  public default EvaluateResult mortarFork(EvaluationContext context, Location location) {
    return new EvaluateResult(null, null, this);
  }
}
