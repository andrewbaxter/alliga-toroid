package com.zarbosoft.alligatoroid.compiler.jvm.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.errors.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMClassInstanceType;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMPseudoFieldMeta;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public class JVMPseudoStaticFieldValue implements JVMDataValue {
  public final JVMPseudoFieldMeta type;

  private JVMPseudoStaticFieldValue(JVMPseudoFieldMeta type) {
    this.type = type;
  }

  public static JVMPseudoStaticFieldValue createJVMPseudoStaticFieldValue(JVMPseudoFieldMeta type) {
    final JVMPseudoStaticFieldValue out = new JVMPseudoStaticFieldValue(type);
    return out;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    JVMUtils.MethodSpecDetails method =
        JVMClassInstanceType.findMethod(context, location, type.methods, type.name, argument);
    if (method == null) {
      return EvaluateResult.error;
    }
    JVMSharedCode pre = new JVMSharedCode();
    JVMSharedCode code = new JVMSharedCode();
    JVMSharedCode post = new JVMSharedCode();
    JVMTargetModuleContext.convertFunctionRootArgument(
        context, location, pre, code, post, argument);
    code.add(
        JVMSharedCode.callStaticMethod(
            context.sourceLocation(location), type.base.jvmName, type.name, method.jvmSigDesc));
    if (method.returnType == null) return new EvaluateResult(code, null, nullValue);
    else return EvaluateResult.pure(method.returnType.stackAsValue(code));
  }

  @Override
  public JVMProtocode jvmCode(EvaluationContext context, Location location) {
    if (!assertData(context, location)) return null;
    return JVMProtocode.of(
        new JVMSharedCode()
            .add(
                JVMSharedCode.accessStaticField(
                    context.sourceLocation(location),
                    type.base.jvmName,
                    type.name,
                    type.data.type.jvmDesc())),
        null);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    if (!assertData(context, location)) return EvaluateResult.error;
    final JVMProtocode code = jvmCode(context, location);
    if (code == null) return EvaluateResult.error;
    return type.data.type.valueAccess(context, location, code, field);
  }

  @Override
  public JVMType jvmType() {
    return type.data.type;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    if (context.target.id() == MortarTargetModuleContext.ID) {
      return new ROPair<>(null, new SimpleBinding(this));
    } else if (context.target.id() == JVMTargetModuleContext.ID) {
      if (!assertData(context, location)) return new ROPair<>(null, ErrorValue.binding);
      final JVMProtocode code = jvmCode(context, location);
      if (code == null) return new ROPair<>(null, ErrorValue.binding);
      return type.data.type.valueBind(context, code);
    } else throw new Assertion();
  }

  private boolean assertData(EvaluationContext context, Location location) {
    if (type.data == null) {
      context.moduleContext.errors.add(JVMError.noDataField(location, type.name));
      return false;
    }
    return true;
  }
}
