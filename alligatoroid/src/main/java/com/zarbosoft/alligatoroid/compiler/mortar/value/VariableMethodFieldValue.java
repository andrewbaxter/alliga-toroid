package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarAutoObjectType;

import static com.zarbosoft.alligatoroid.compiler.mortar.MortarBuiltin.nullType;
import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class VariableMethodFieldValue implements Value, NoExportValue {
  private final MortarMethodFieldType type;
  private final MortarAutoObjectType base;
  private final MortarCarry carry;

  public VariableMethodFieldValue(
      MortarAutoObjectType base, MortarCarry carry, MortarMethodFieldType type) {
    this.type = type;
    this.carry = carry;
    this.base = base;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return carry.drop(context, location);
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    if (!MortarTargetModuleContext.assertTarget(context, location)) return EvaluateResult.error;
    if (argument == ErrorValue.error) return EvaluateResult.error;
    JVMSharedCode pre = new JVMSharedCode();
    JVMSharedCode code = new JVMSharedCode().add(carry.half(context));
    JVMSharedCode post = new JVMSharedCode();
    if (type.funcInfo.needsModule)
      code.add(((MortarTargetModuleContext) context.target).transfer(context.moduleContext));
    MortarTargetModuleContext.convertFunctionArgumentRoot(
        context, location, pre, code, post, argument);
    code.add(
        JVMSharedCode.callMethod(
            context.sourceLocation(location),
            base.jvmName,
            type.funcInfo.method.getName(),
            type.funcInfo.descriptor));
    if (type.funcInfo.returnType == nullType)
      return new EvaluateResult(pre.add(code), post, nullValue);
    else return new EvaluateResult(pre, post, type.funcInfo.returnType.stackAsValue(code));
  }
}
