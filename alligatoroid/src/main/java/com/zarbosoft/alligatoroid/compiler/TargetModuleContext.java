package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface TargetModuleContext {
  public TargetCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> values);

  Id id();

  EvaluateResult vary(EvaluationContext context, Location id, Value value);

  boolean isCodeEmpty(TargetCode code);

  public abstract static class Id {
    @Override
    public abstract String toString();
  }
}
