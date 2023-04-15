package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeCatch;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableDeferred;

public interface MortarDataBindstate {
  Value bindstate_constAsValue(Object value);

  default Value bindstate_loadBinding(MortarDataVarBinding binding) {
    return new MortarDataValueVariableDeferred(
            binding.bindstate.bindstate_load(),
            new MortarDeferredCodeBinding(
                    bindstate_loadBytecode(binding.key), bindstate_storeBytecode(binding.key)));
  }

  MortarDataTypestate bindstate_load();

  JavaBytecode bindstate_storeBytecode(JavaBytecodeBindingKey key);

  JavaBytecode bindstate_loadBytecode(JavaBytecodeBindingKey key);

  MortarDataBindstate bindstate_fork();

  boolean bindstate_bindMerge(
      EvaluationContext context, Location location, Binding other, Location otherLocation);

  /** Actual drop code, not including finally/jumps/etc */
  default JavaBytecode bindstate_varBindDropInner(
      EvaluationContext context, Location location, MortarDataVarBinding mortarDataBinding) {
    return null;
  }

  default JavaBytecode bindstate_varBindDrop(
      EvaluationContext context, Location location, MortarDataVarBinding mortarDataBinding) {
    final JavaBytecode inner = bindstate_varBindDropInner(context, location, mortarDataBinding);
    if (inner == null) {
      return null;
    }
    return new JavaBytecodeCatch(mortarDataBinding.finallyKey, inner);
  }
}
