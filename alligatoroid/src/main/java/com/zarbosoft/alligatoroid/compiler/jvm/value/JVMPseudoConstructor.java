package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.rendaw.common.ROTuple;

import static com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfClassType.getArgTuple;

public class JVMPseudoConstructor
    implements SimpleValue, AutoBuiltinExportable, LeafExportable, JVMOkValue {
  public final JVMHalfClassType base;

  @Override
  public Location location() {
    return SimpleValue.super.location();
  }

  public JVMPseudoConstructor(JVMHalfClassType base) {
    this.base = base;
  }

  @Override
  public EvaluateResult jvmCall(
      EvaluationContext context, Location location, MortarValue argument) {
    if (!base.resolveInternals(context, location)) return EvaluateResult.error;
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
            context.sourceLocation(location), base.jvmName, real.jvmSigDesc, argCode);
    if (real.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(real.returnType.stackAsValue(code));
  }

  @Override
  public TargetCode jvmDrop(EvaluationContext context, Location location) {
    return null;
  }
}
