package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class MortarDataGenericBindingConst implements Binding {
  public final MortarDataTypestate typestate;
  public Object value;

  public MortarDataGenericBindingConst(MortarDataTypestate typestate, Object value) {
    this.typestate = typestate;
    this.value = value;
  }

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(typestate.typestate_constAsValue(value));
  }

  @Override
  public Binding fork() {
    return new MortarDataGenericBindingConst(typestate.typestate_fork(), value);
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public boolean merge(
      EvaluationContext context, Location location, Binding other, Location otherLocation) {
    return typestate.typestate_bindMerge(
        context, location, ((MortarDataGenericBindingConst) other).typestate, otherLocation);
  }
}
