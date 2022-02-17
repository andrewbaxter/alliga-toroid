package com.zarbosoft.alligatoroid.compiler.jvm.mortartypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.errors.JVMError;
import com.zarbosoft.alligatoroid.compiler.jvm.modelother.JVMConstructor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.SingletonBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarImmutableType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectType;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.alligatoroid.compiler.jvm.JVMTargetModuleContext.correspondJvmTypeTuple;

public class JVMConstructorType extends MortarObjectType implements SingletonBuiltinExportable {
  public static final JVMConstructorType type = new JVMConstructorType();
  public static final JVMSharedDataDescriptor DESC =
      JVMSharedDataDescriptor.fromObjectClass(JVMConstructor.class);

  private JVMConstructorType() {}

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (type != this.type) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }

  @Override
  public EvaluateResult constCall(
      EvaluationContext context, Location location, Object inner, Value argument) {
    if (!JVMTargetModuleContext.assertTarget(context, location)) return EvaluateResult.error;
    JVMConstructor meta = (JVMConstructor) inner;
    if (!meta.type.resolveInternals(context, location)) return EvaluateResult.error;
    ROTuple argTuple = correspondJvmTypeTuple(argument);
    final JVMUtils.MethodSpecDetails constructor = meta.type.constructors.getOpt(argTuple);
    if (constructor == null) {
      context.moduleContext.errors.add(JVMError.noConstructorMatchingParameters(location));
      return EvaluateResult.error;
    }
    JVMSharedCode pre = new JVMSharedCode();
    JVMSharedCode code = new JVMSharedCode();
    JVMSharedCode post = new JVMSharedCode();
    JVMTargetModuleContext.convertFunctionRootArgument(
        context, location, pre, code, post, argument);
    code.add(
        JVMSharedCode.instantiate(
            context.sourceLocation(location), meta.type.jvmName, constructor.jvmSigDesc, code));
    return new EvaluateResult(pre, post, constructor.returnType.stackAsValue(code));
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return DESC;
  }
}
