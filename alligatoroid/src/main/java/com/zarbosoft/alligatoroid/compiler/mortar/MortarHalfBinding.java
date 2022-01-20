package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeStoreLoad;
import com.zarbosoft.alligatoroid.compiler.model.MortarBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfDataType;

public class MortarHalfBinding implements MortarBinding {
  public final Object key;
  public final MortarHalfDataType type;

  public MortarHalfBinding(Object key, MortarHalfDataType type) {
    this.key = key;
    this.type = type;
  }

  @Override
  public EvaluateResult mortarFork(EvaluationContext context, Location location) {
    return EvaluateResult.pure(
        type.asValue(
            location,
            new MortarProtocode() {
              @Override
              public JVMSharedCodeElement mortarHalfLower(EvaluationContext context) {
                return new JVMSharedCodeStoreLoad(type.loadOpcode(), key);
              }

              @Override
              public JVMSharedCodeElement mortarDrop(EvaluationContext context, Location location) {
                return null;
              }
            }));
  }

  @Override
  public TargetCode mortarDrop(EvaluationContext context, Location location) {
    return null;
  }
}
