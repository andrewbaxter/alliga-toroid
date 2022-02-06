package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.BindingKey;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;

public class MortarDataBinding implements Binding {
  public final BindingKey key;
  public final MortarDataType type;

  public MortarDataBinding(BindingKey key, MortarDataType type) {
    this.key = key;
    this.type = type;
  }

  @Override
  public EvaluateResult fork(EvaluationContext context, Location location) {
    return EvaluateResult.pure(type.boundAsValue(this));
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return null;
  }

}
