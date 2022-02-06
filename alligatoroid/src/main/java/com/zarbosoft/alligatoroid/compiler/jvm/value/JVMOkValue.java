package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.BindNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.error.CallNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.rendaw.common.ROPair;

public interface JVMOkValue extends JVMValue {
  @Override
  public default EvaluateResult jvmCall(
      EvaluationContext context, Location location, VariableDataStackValue argument) {
    context.moduleContext.errors.add(new CallNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public default EvaluateResult jvmAccess(
      EvaluationContext context, Location location, VariableDataStackValue field) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public default ROPair<TargetCode, ? extends Binding> jvmBind(
      EvaluationContext context, Location location) {
    context.moduleContext.errors.add(new BindNotSupported(location));
    return new ROPair<>(null, null);
  }
}
