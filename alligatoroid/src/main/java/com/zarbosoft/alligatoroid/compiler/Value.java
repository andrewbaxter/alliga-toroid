package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.BindNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.CallNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.ExportNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.SetNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.VaryNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public interface Value extends Exportable {
  TargetCode drop(EvaluationContext context, Location location);

  public default EvaluateResult export(EvaluationContext context, Location location) {
    context.moduleContext.errors.add(new ExportNotSupported(location));
    return EvaluateResult.error;
  }

  /**
   * Results in VariableDataValue
   *
   * @param context
   * @param location
   * @return
   */
  public default EvaluateResult vary(EvaluationContext context, Location location) {
    context.moduleContext.errors.add(new VaryNotSupported(location));
    return EvaluateResult.error;
  }

  public default EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    context.moduleContext.errors.add(new CallNotSupported(location));
    return EvaluateResult.error;
  }

  public default EvaluateResult access(EvaluationContext context, Location location, Value field) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  /**
   * Creates a value to put in the scope. If error, return error value, null (add error to context).
   *
   * @param context
   * @param location
   * @return side effect, binding
   */
  public default ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    context.moduleContext.errors.add(new BindNotSupported(location));
    return new ROPair<>(null, ErrorValue.binding);
  }

  default EvaluateResult set(EvaluationContext context, Location location, Value value) {
    context.moduleContext.errors.add(new SetNotSupported(location));
    return EvaluateResult.error;
  }

  default ROList<String> traceFields(EvaluationContext context, Location location) {
    return ROList.empty;
  }
}
