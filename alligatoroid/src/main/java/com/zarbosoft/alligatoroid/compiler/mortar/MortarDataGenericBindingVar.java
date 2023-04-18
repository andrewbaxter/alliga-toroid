package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableDeferred;

public class MortarDataGenericBindingVar implements Binding {
  public final JavaBytecodeBindingKey key;
  public final MortarDataTypestate typestate;

  public MortarDataGenericBindingVar(JavaBytecodeBindingKey key, MortarDataTypestate typestate) {
    this.key = key;
    this.typestate = typestate;
  }

  @Override
  public EvaluateResult load(EvaluationContext context, Location location) {
    return EvaluateResult.pure(
        new MortarDataValueVariableDeferred(
            typestate.typestate_fork(),
            new MortarDeferredCodeBinding(
                typestate.typestate_loadBytecode(key), typestate.typestate_storeBytecode(key))));
  }

  @Override
  public Binding fork() {
    return new MortarDataGenericBindingVar(key, typestate.typestate_fork());
  }

  @Override
  public TargetCode dropCode(EvaluationContext context, Location location) {
    return new MortarTargetCode(typestate.typestate_varBindDrop(context, location, this));
  }

  @Override
  public boolean merge(
      EvaluationContext context, Location location, Binding other, Location otherLocation) {
    return typestate.typestate_bindMerge(
        context, location, ((MortarDataGenericBindingVar) other).typestate, otherLocation);
  }
}
