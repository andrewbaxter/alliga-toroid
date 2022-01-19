package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarHalfMethodType;

public class MortarMethodField implements SimpleValue, NoExportValue, Exportable {
  private final MortarProtocode lower;
  private final MortarHalfMethodType type;

  public MortarMethodField(MortarProtocode lower, MortarHalfMethodType type) {
    this.lower = lower;
    this.type = type;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return lower.drop(context, location);
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    if (argument == ErrorValue.error) return EvaluateResult.error;
    JVMSharedCode code = new JVMSharedCode().add(lower.lower(context));
    if (type.needsModule)
      code.add(((MortarTargetModuleContext) context.target).transfer(context.moduleContext));
    MortarTargetModuleContext.convertFunctionArgument(context, code, argument);
    code.add(
        JVMSharedCode.callMethod(
            context.sourceLocation(location), type.base.jvmName, type.name, type.jbcDesc));
    if (type.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(type.returnType.stackAsValue(code));
  }
}
