package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class NullValue implements Value {
  public static Value value = new NullValue();

  private NullValue() {}

  @Override
  public EvaluateResult vary(EvaluationContext context, Location id) {
    return EvaluateResult.pure(this);
  }

  @Override
  public boolean canCastTo(EvaluationContext context, AlligatorusType type) {
    return type == NullType.type;
  }

  @Override
  public EvaluateResult castTo(EvaluationContext context, Location location, AlligatorusType type) {
    return EvaluateResult.pure(this);
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
  return EvaluateResult.pure(this);
  }

  @Override
  public AlligatorusType type(EvaluationContext context) {
  return NullType.type;
  }
}
