package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.ModuleCompileContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Desemiserializer;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMHalfDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ErrorBinding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.value.SimpleValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.NullValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.ROTuple;

import static com.zarbosoft.alligatoroid.compiler.jvm.value.JVMHalfClassType.getArgTuple;

public class JVMPseudoStaticField implements SimpleValue, AutoExportable {
  public final String name;
  public JVMHalfClassType base;

  public JVMPseudoStaticField(JVMHalfClassType base, String name) {
    this.base = base;
    this.name = name;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    ROTuple argTuple = getArgTuple(argument);
    JVMMethodFieldType real = base.methodFields.getOpt(ROTuple.create(name).append(argTuple));
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noMethodField(location, name));
      return EvaluateResult.error;
    }
    JVMSharedCode code = new JVMSharedCode();
    JVMTargetModuleContext.convertFunctionArgument(context, code, argument);
    code.add(
        JVMSharedCode.callStaticMethod(
            context.sourceLocation(location), base.name, name, real.specDetails.jvmSigDesc));
    if (real.specDetails.returnType == null) return new EvaluateResult(code, null, NullValue.value);
    else return EvaluateResult.pure(real.specDetails.returnType.stackAsValue((JVMSharedCode) code));
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
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
            return null;
          }

          @Override
          public JVMSharedCodeElement lower(EvaluationContext context) {
            return JVMSharedCode.accessStaticField(
                context.sourceLocation(location), base.name, name, real.jvmDesc());
          }
        });
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    JVMHalfDataType real = base.dataFields.getOpt(name);
    if (real == null) {
      context.moduleContext.errors.add(JVMError.noDataField(location, name));
      return new ROPair<>(null, ErrorBinding.binding);
    }
    return real.valueBind(
        JVMSharedCode.accessStaticField(
            context.sourceLocation(location), base.name, name, real.jvmDesc()));
  }

  @Override
  public Value graphDeserializeValue(
      ModuleCompileContext context,
      Desemiserializer typeDesemiserializer,
      SemiserialSubvalue data) {
    throw new Assertion();
  }
}
