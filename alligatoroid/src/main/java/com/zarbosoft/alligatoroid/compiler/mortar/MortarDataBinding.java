package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatchKey;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class MortarDataBinding implements Binding {
  public final JavaBytecodeBindingKey key;
  public final MortarDataTypestate typestate;
  public final JavaBytecodeCatchKey finallyKey;

  public MortarDataBinding(
      JavaBytecodeBindingKey key, MortarDataTypestate typestate, JavaBytecodeCatchKey finallyKey) {
    this.key = key;
    this.typestate = typestate;
    this.finallyKey = finallyKey;
  }

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(typestate.typestate_loadBinding(this));
  }

  @Override
  public Binding fork() {
    return new MortarDataBinding(key, typestate.typestate_fork(), finallyKey);
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return new MortarTargetCode(typestate.typestate_varBindDrop(context, location, this));
  }

  @Override
  public boolean merge(
      EvaluationContext context, Location location, Binding other, Location otherLocation) {
    return typestate.typestate_varBindMerge(context, location, other, otherLocation);
  }
}
