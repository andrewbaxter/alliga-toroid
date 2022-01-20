package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.model.MortarBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public interface MortarValue extends Exportable, Value {
  EvaluateResult mortarCall(EvaluationContext context, Location location, MortarValue argument);

  EvaluateResult mortarAccess(EvaluationContext context, Location location, MortarValue field);

  TargetCode mortarDrop(EvaluationContext context, Location location);

  /**
   * Creates a value to put in the scope. If error, return error value, null (add error to context).
   *
   * @param context
   * @param location
   * @return side effect, binding
   */
  ROPair<TargetCode, MortarBinding> mortarBind(EvaluationContext context, Location location);
}
