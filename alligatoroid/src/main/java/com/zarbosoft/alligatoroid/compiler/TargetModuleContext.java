package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface TargetModuleContext {
  public TargetCode merge(
      EvaluationContext context, Location location, Iterable<TargetCode> values);

  Id id();

  TargetCode codeLand(JumpKey jumpKey);

  TargetCode codeJump(JumpKey jumpKey);

  public abstract static class Id {
    @Override
    public abstract String toString();
  }
}
