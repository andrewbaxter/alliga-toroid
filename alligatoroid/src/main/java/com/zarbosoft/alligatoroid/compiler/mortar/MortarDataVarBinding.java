package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatchKey;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class MortarDataVarBinding implements Binding {
  public final JavaBytecodeBindingKey key;
  public final MortarDataBindstate bindstate;
  public final JavaBytecodeCatchKey finallyKey;

  public MortarDataVarBinding(
          JavaBytecodeBindingKey key, MortarDataBindstate bindstate, JavaBytecodeCatchKey finallyKey) {
    this.key = key;
    this.bindstate = bindstate;
    this.finallyKey = finallyKey;
  }

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(bindstate.bindstate_loadBinding(this));
  }

  @Override
  public Binding fork() {
    return new MortarDataVarBinding(key, bindstate.bindstate_fork(), finallyKey);
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return new MortarTargetCode(bindstate.bindstate_varBindDrop(context, location, this));
  }

  @Override
  public boolean merge(
      EvaluationContext context, Location location, Binding other, Location otherLocation) {
    return bindstate.bindstate_bindMerge(context, location, other, otherLocation);
  }
}
