package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

public class UnreachableValue implements Value {
  public static final UnreachableValue value = new UnreachableValue();

  private UnreachableValue() {}

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location id) {
  context.errors.add(new GeneralLocationError(id, "This tree never exits directly, result can't be used in an expression context"));
  return EvaluateResult.error;
  }

  @Override
  public boolean canCastTo(AlligatorusType type) {
  return false;
  }

  @Override
  public EvaluateResult castTo(EvaluationContext context, Location location, AlligatorusType type) {
  throw new Assertion();
  }

  @Override
  public Value unfork(EvaluationContext context, Location location, ROPair<Location, Value> other) {
    throw new Assertion();
  }
}
