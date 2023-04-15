package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class ConstBinding implements Binding {
  public final MortarDataBindstate bindstate;
  public Object value;

  public ConstBinding(MortarDataBindstate bindstate, Object value) {
    this.bindstate = bindstate;
    this.value = value;
  }

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(bindstate.bindstate_constAsValue(value));
  }

  @Override
  public Binding fork() {
    return new ConstBinding(bindstate.bindstate_fork(), value);
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public boolean merge(
      EvaluationContext context, Location location, Binding other, Location otherLocation) {
    return bindstate.bindstate_bindMerge(context, location, other, otherLocation);
  }
}
