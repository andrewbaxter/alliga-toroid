package com.zarbosoft.alligatoroid.compiler.mortar.value.base;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.GraphValue;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public interface Value extends GraphValue {
  Value type();

  /**
   * Semiserializable - provides implementation of graph + co methods (GraphValue)
   *
   * @return
   */
  boolean canExport();

  EvaluateResult call(EvaluationContext context, Location location, Value argument);

  EvaluateResult access(EvaluationContext context, Location location, Value field);

  default EvaluateResult evaluate(EvaluationContext context) {
    return new EvaluateResult(null, null, this);
  }

  TargetCode drop(EvaluationContext context, Location location);

  /**
   * Creates a value to put in the scope. If error, return error value, null (add error to context).
   *
   * @param context
   * @param location
   * @return side effect, binding
   */
  ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location);

  /**
   * Location or null
   *
   * @return
   */
  Location location();
}
