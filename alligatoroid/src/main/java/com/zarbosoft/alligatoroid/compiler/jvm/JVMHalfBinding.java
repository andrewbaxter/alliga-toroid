package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.MortarBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class JVMHalfBinding implements Binding {
  public final Object key;
  public final JVMHalfDataType type;

  public JVMHalfBinding(Object key, JVMHalfDataType type) {
    this.key = key;
    this.type = type;
  }

  public EvaluateResult jvmFork(EvaluationContext context, Location location) {
    return EvaluateResult.pure(
        type.asValue(
            new JVMProtocode() {
              @Override
              public JVMSharedCodeElement jvmLower(EvaluationContext context) {
                return new JVMSharedCode().addVarInsn(type.loadOpcode(), key);
              }

              @Override
              public JVMSharedCodeElement jvmDrop(EvaluationContext context, Location location) {
                return null;
              }
            }));
  }

  public TargetCode jvmDrop(EvaluationContext context, Location location) {
    return null;
  }
}
