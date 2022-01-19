package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NoExportValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ROTuple;

import static com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfClassType.getArgTuple;

public class JVMPseudoField implements SimpleValue, NoExportValue, Exportable {
  public final JVMHalfClassType base;
  public final String name;
  private final JVMProtocode lower;

  public JVMPseudoField(JVMProtocode lower, JVMHalfClassType base, String name) {
    this.lower = lower;
    this.base = base;
    this.name = name;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return lower.drop(context, location);
  }

  @Override
  public Value type() {
    throw new Assertion();
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    if (!base.resolveInternals(context, location)) return EvaluateResult.error;
    ROTuple argTuple = getArgTuple(argument);
    JVMMethodFieldType real = base.methodFields.getOpt(ROTuple.create(name).append(argTuple));
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noMethodField(location, name));
      return EvaluateResult.error;
    }
    JVMSharedCode code = new JVMSharedCode().add(lower.lower(context));
    JVMTargetModuleContext.convertFunctionArgument(context, code, argument);
    code.add(
        JVMSharedCode.callMethod(
            context.sourceLocation(location), base.jvmName, name, real.specDetails.jvmSigDesc));
    if (real.specDetails.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(real.specDetails.returnType.stackAsValue(code));
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    if (!base.resolveInternals(context, location)) return EvaluateResult.error;
    JVMHalfDataType real = base.dataFields.getOpt(name);
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noDataField(location, name));
      return EvaluateResult.error;
    }
    return real.valueAccess(
        context,
        location,
        field,
        new JVMProtocode() {
          @Override
          public JVMSharedCodeElement drop(EvaluationContext context, Location location) {
            return lower.drop(context, location);
          }

          @Override
          public JVMSharedCodeElement lower(EvaluationContext context) {
            return JVMSharedCode.accessField(
                context.sourceLocation(location), base.jvmName, name, real.jvmDesc());
          }
        });
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    if (!base.resolveInternals(context, location)) return new ROPair<>(null, ErrorBinding.binding);
    JVMHalfDataType real = base.dataFields.getOpt(name);
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noDataField(location, name));
      return new ROPair<>(null, ErrorBinding.binding);
    }
    return real.valueBind(
        new JVMSharedCode()
            .add(lower.lower(context))
            .add(
                JVMSharedCode.accessField(
                    context.sourceLocation(location), base.jvmName, name, real.jvmDesc())));
  }
}
