package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class MortarDataBinding implements Binding {
  public final JavaBytecodeBindingKey key;
  public final MortarDataTypestate type;

  public MortarDataBinding(JavaBytecodeBindingKey key, MortarDataTypestate type) {
    this.key = key;
    this.type = type;
  }

  @Override
  public EvaluateResult fork(EvaluationContext context, Location location) {
    return EvaluateResult.pure(type.typestate_forkBinding(this));
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return null;
  }
}
