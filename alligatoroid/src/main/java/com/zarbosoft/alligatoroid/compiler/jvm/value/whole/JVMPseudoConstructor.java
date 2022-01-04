package com.zarbosoft.alligatoroid.compiler.jvm.value.whole;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.NullValue;
import com.zarbosoft.rendaw.common.ROTuple;

import static com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMClassType.getArgTuple;

public class JVMPseudoConstructor implements SimpleValue, AutoGraphMixin, LeafValue {
  public final JVMClassType base;

  public JVMPseudoConstructor(JVMClassType base) {
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
