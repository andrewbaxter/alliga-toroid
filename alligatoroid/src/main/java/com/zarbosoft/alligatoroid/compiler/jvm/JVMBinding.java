package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.modules.Module;
import com.zarbosoft.alligatoroid.compiler.TargetCode;

public class JVMBinding implements Binding {
  public final Object key;
  public final JVMDataType type;

  public JVMBinding(Object key, JVMDataType type) {
    this.key = key;
    this.type = type;
  }

  @Override
  public EvaluateResult fork(EvaluationContext context, Location location) {
    return EvaluateResult.pure(
        type.asValue(
            new JVMProtocode() {
              @Override
              public JVMCode lower(EvaluationContext context) {
                return (JVMCode) new JVMCode().addVarInsn(type.loadOpcode(), key);
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
