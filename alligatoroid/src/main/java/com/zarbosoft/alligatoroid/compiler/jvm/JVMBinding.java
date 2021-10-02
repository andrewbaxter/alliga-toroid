package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Binding;
import com.zarbosoft.alligatoroid.compiler.Context;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.TargetCode;

public class JVMBinding implements Binding {
  public final Object key;
  public final JVMDataType type;

  public JVMBinding(Object key, JVMDataType type) {
    this.key = key;
    this.type = type;
  }

  @Override
  public EvaluateResult fork(Context context, Location location) {
    return EvaluateResult.pure(
        type.asValue(
            new JVMProtocode() {
              @Override
              public JVMCode lower(Module module) {
                return (JVMCode) new JVMCode().addVarInsn(type.loadOpcode(module), key);
              }

              @Override
              public TargetCode drop(Context context, Location location) {
                return null;
              }
            }));
  }

  @Override
  public TargetCode drop(Context context, Location location) {
    return null;
  }
}
