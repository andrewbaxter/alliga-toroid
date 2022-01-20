package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.rendaw.common.ROPair;

public interface JVMValue extends Value {
  EvaluateResult jvmCall(EvaluationContext context, Location location, MortarValue argument);

  EvaluateResult jvmAccess(EvaluationContext context, Location location, MortarValue field);

  TargetCode jvmDrop(EvaluationContext context, Location location);

  ROPair<TargetCode, ? extends Binding> jvmBind(EvaluationContext context, Location location);

  @Override
  default Location location() {
    return null;
  }
}
