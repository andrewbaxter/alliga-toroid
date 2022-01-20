package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROPair;

public interface TargetModuleContext {
  public TargetCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> values);

  ROPair<TargetCode, ? extends Binding> bind(
      EvaluationContext context, Location location, Value value);

  EvaluateResult call(EvaluationContext context, Location location, Value target, Value args);

  EvaluateResult access(EvaluationContext context, Location location, Value target, Value field);

  EvaluateResult fork(EvaluationContext context, Location location, Binding binding);

  TargetCode drop(EvaluationContext context, Location location, Value value);

  TargetCode drop(EvaluationContext context, Location location, Binding binding);
}
