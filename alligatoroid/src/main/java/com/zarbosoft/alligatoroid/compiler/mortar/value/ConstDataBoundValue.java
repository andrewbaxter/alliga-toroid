package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.ConstBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;

public class ConstDataBoundValue extends ConstDataValue {
  private final ConstBinding binding;

  public ConstDataBoundValue(ConstBinding binding) {
    this.binding = binding;
  }

  @Override
  public MortarDataType mortarType() {
    return binding.type;
  }

  @Override
  public MortarCarry mortarVaryCode(EvaluationContext context, Location location) {
    return MortarCarry.ofDeferredHalf(c -> binding.type.constValueVary(context, binding.value));
  }

  @Override
  public Object getInner() {
    return binding.value;
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    return setHelper(
        this,
        context,
        location,
        value,
        v -> {
          binding.value = v;
        });
  }
}
