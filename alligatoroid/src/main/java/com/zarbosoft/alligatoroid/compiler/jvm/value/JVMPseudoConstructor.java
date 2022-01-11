package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NullValue;
import com.zarbosoft.rendaw.common.ROTuple;

import static com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfClassType.getArgTuple;

public class JVMPseudoConstructor implements SimpleValue, AutoGraphMixin, LeafValue {
  public final JVMHalfClassType base;

  public JVMPseudoConstructor(JVMHalfClassType base) {
    this.base = base;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    base.resolveMethods(context);
    ROTuple argTuple = getArgTuple(argument);
    JVMUtils.MethodSpecDetails real = base.constructors.getOpt(argTuple);
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noConstructorMatchingParameters(location));
      return EvaluateResult.error;
    }
    JVMSharedCode argCode = new JVMSharedCode();
    JVMTargetModuleContext.convertFunctionArgument(context, argCode, argument);
    JVMSharedCodeElement code =
        JVMSharedCode.instantiate(
            context.sourceLocation(location), base.name, real.jvmSigDesc, argCode);
    if (real.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(real.returnType.stackAsValue(code));
  }
}
