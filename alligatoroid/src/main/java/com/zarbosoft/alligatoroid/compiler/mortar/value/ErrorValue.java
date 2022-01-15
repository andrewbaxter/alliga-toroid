package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.LeafExportable;
import com.zarbosoft.rendaw.common.ROPair;

public final class ErrorValue implements Value, NoExportValue, LeafExportable {
  public static final ErrorValue error = new ErrorValue();

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
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, ErrorBinding.binding);
  }

  @Override
  public Location location() {
    return null;
  }
}
