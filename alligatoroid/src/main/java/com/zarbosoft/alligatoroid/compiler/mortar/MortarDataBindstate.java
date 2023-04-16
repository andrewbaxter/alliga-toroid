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

  Value bindstate_loadBinding(MortarDataVarBinding binding);


  MortarDataBindstate bindstate_fork();

  boolean bindstate_bindMerge(
      EvaluationContext context, Location location, Binding other, Location otherLocation);


  JavaBytecode bindstate_varBindDrop(
      EvaluationContext context, Location location, MortarDataVarBinding mortarDataBinding);
}
