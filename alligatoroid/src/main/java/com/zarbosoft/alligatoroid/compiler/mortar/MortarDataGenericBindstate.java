package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatch;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableDeferred;

public class MortarDataGenericBindstate implements MortarDataBindstate {
  public final MortarDataTypestate typestate;

  public MortarDataGenericBindstate(MortarDataTypestate typestate) {
    this.typestate = typestate;
  }
  @Override
  public JavaBytecode bindstate_varBindDrop(
          EvaluationContext context, Location location, MortarDataVarBinding mortarDataBinding) {
    final JavaBytecode inner = typestate.typestate_varBindDrop(context, location, mortarDataBinding);
    if (inner == null) {
      return null;
    }
    return new JavaBytecodeCatch(mortarDataBinding.finallyKey, inner);
  }

  @Override
  public Value bindstate_loadBinding(MortarDataVarBinding binding) {
    return new MortarDataValueVariableDeferred(
        typestate.typestate_fork(),
        new MortarDeferredCodeBinding(
            typestate.typestate_loadBytecode(binding.key),
            typestate.typestate_storeBytecode(binding.key)));
  }

  @Override
  public Value bindstate_constAsValue(Object value) {
    return new MortarDataValueConst(typestate.typestate_fork(), value);
  }

  @Override
  public MortarDataBindstate bindstate_fork() {
    return new MortarDataGenericBindstate(typestate.typestate_fork());
  }

  @Override
  public boolean bindstate_bindMerge(
      EvaluationContext context, Location location, Binding other, Location otherLocation) {
    return typestate.typestate_bindMerge(
        context, location, ((MortarDataGenericBindstate) other).typestate, otherLocation);
  }
}
