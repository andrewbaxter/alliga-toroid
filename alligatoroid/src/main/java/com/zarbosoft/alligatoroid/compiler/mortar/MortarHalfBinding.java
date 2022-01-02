package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;

public class MortarHalfBinding implements Binding {
  public final Object key;
  public final MortarHalfDataType type;

  public MortarHalfBinding(Object key, MortarHalfDataType type) {
    this.key = key;
    this.type = type;
  }

  @Override
  public EvaluateResult fork(EvaluationContext context, Location location) {
    return EvaluateResult.pure(
        type.asValue(
            new MortarProtocode() {
              @Override
              public MortarCode lower() {
                return (MortarCode) new MortarCode().addVarInsn(type.loadOpcode(), key);
              }

              @Override
              public TargetCode drop(EvaluationContext context, Location location) {
                return null;
              }
            }));
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }
}
