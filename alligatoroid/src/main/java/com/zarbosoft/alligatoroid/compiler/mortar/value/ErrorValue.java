package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.NullType;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleBinding;
import com.zarbosoft.rendaw.common.ROPair;

public final class ErrorValue implements Value, NoExportValue {
  public static final ErrorValue value = new ErrorValue();
  public static final Binding binding = new SimpleBinding(value);

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
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, binding);
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    return EvaluateResult.error;
  }
  @Override
  public AlligatorusType type(EvaluationContext context) {
  return NullType.INST;
  }
}
