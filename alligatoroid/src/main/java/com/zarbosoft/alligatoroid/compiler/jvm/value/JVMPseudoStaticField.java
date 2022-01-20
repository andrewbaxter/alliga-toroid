package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.LeafExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMMethodFieldType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.rendaw.common.ROPair;

public class JVMPseudoStaticField
    implements SimpleValue, AutoBuiltinExportable, LeafExportable, JVMValue {
  public final String name;
  public JVMHalfClassType base;

  public JVMPseudoStaticField(JVMHalfClassType base, String name) {
    this.base = base;
    this.name = name;
  }

  @Override
  public Location location() {
    return SimpleValue.super.location();
  }

  @Override
  public TargetCode jvmDrop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult jvmCall(
      EvaluationContext context, Location location, MortarValue argument) {
    JVMMethodFieldType real =
        base.findMethod(context, location, base.staticMethodFields, name, argument);
    if (real == null) {
      return EvaluateResult.error;
    }
    JVMSharedCode code = new JVMSharedCode();
    JVMTargetModuleContext.convertFunctionArgument(context, code, argument);
    code.add(
        JVMSharedCode.callStaticMethod(
            context.sourceLocation(location), base.jvmName, name, real.specDetails.jvmSigDesc));
    if (real.specDetails.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(real.specDetails.returnType.stackAsValue((JVMSharedCode) code));
  }

  @Override
  public EvaluateResult jvmAccess(EvaluationContext context, Location location, MortarValue field) {
    if (!base.resolveInternals(context, location)) return EvaluateResult.error;
    JVMHalfDataType real = base.staticDataFields.getOpt(name);
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
          public JVMSharedCodeElement jvmDrop(EvaluationContext context, Location location) {
            return null;
          }

          @Override
          public JVMSharedCodeElement jvmLower(EvaluationContext context) {
            return JVMSharedCode.accessStaticField(
                context.sourceLocation(location), base.jvmName, name, real.jvmDesc());
          }
        });
  }

  @Override
  public ROPair<TargetCode, ? extends Binding> jvmBind(
      EvaluationContext context, Location location) {
    if (!base.resolveInternals(context, location)) return new ROPair<>(null, ErrorBinding.binding);
    JVMHalfDataType real = base.staticDataFields.getOpt(name);
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noDataField(location, name));
      return new ROPair<>(null, ErrorBinding.binding);
    }
    return real.valueBind(
        JVMSharedCode.accessStaticField(
            context.sourceLocation(location), base.jvmName, name, real.jvmDesc()));
  }
}
