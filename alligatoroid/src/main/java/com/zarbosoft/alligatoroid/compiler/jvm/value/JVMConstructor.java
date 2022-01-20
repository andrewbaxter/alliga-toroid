package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.rendaw.common.Assertion;

public class JVMConstructor
    implements SimpleValue, AutoBuiltinExportable, LeafExportable, JVMOkValue {
  private final Record spec;
  public JVMHalfClassType base;
  public JVMUtils.MethodSpecDetails specDetails;

  @Override
  public Location location() {
    return SimpleValue.super.location();
  }

  public JVMConstructor(JVMHalfClassType base, Record spec) {
    this.base = base;
    this.spec = spec;
  }

  public static JVMConstructor create(JVMHalfClassType base, Record spec) {
    final JVMConstructor out = new JVMConstructor(base, spec);
    out.postInit();
    return out;
  }

  @Override
  public void postInit() {
    specDetails = JVMUtils.methodSpecDetails(spec);
  }

  @Override
  public EvaluateResult mortarCall(
      EvaluationContext context, Location location, MortarValue argument) {
    if (!base.resolveInternals(context, location)) return EvaluateResult.error;
    JVMSharedCode code = new JVMSharedCode();
    JVMTargetModuleContext.convertFunctionArgument(context, code, argument);
    code.add(
        JVMSharedCode.instantiate(
            context.sourceLocation(location), base.jvmName, specDetails.jvmSigDesc, code));
    return new EvaluateResult(code, null, NullValue.value);
  }

  @Override
  public TargetCode jvmDrop(EvaluationContext context, Location location) {
    throw new Assertion();
  }
}
