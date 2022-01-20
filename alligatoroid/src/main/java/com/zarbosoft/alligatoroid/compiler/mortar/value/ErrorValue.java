package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.model.MortarBinding;
import com.zarbosoft.alligatoroid.compiler.model.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public final class ErrorValue implements MortarValue, NoExportValue, Exportable {
  public static final ErrorValue error = new ErrorValue();

  private ErrorValue() {}

  @Override
  public EvaluateResult mortarCall(EvaluationContext context, Location location, MortarValue argument) {
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult mortarAccess(EvaluationContext context, Location location, MortarValue field) {
    return EvaluateResult.error;
  }

  @Override
  public TargetCode mortarDrop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public ROPair<TargetCode, MortarBinding> mortarBind(EvaluationContext context, Location location) {
    return new ROPair<>(null, ErrorBinding.binding);
  }

  @Override
  public Location location() {
    return null;
  }
}
