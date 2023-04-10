package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class ConstBinding implements Binding {
  public final MortarDataTypestate type;
  public Object value;

  public ConstBinding(MortarDataTypestate type, Object value) {
    this.type = type;
    this.value = value;
  }

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(type.typestate_constAsValue(value));
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return null;
  }
}
