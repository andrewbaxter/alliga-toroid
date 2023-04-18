package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataTypestate;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeStack;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarDataValueVariableStack extends MortarDataValue implements NoExportValue {

  public MortarDataValueVariableStack(MortarDataTypestate typestate) {
    super(typestate);
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    final ROPair<JavaBytecode, Binding> binding = typestate.typestate_varBind(context);
    return new ROPair<>(new MortarTargetCode(binding.first), binding.second);
  }

  @Override
  public MortarDataType type() {
    return typestate.typestate_asType();
  }

  @Override
  public MortarTargetCode consume(EvaluationContext context, Location location) {
    return MortarTargetCode.empty;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return new MortarTargetCode(JavaBytecodeUtils.pop);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return typestate.typestate_varAccess(context, location, field, new MortarDeferredCodeStack());
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    context.errors.add(new CantSetStackValue(location));
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location id) {
    return EvaluateResult.pure(this);
  }

  @Override
  public boolean canCastTo(EvaluationContext context, AlligatorusType type) {
    return typestate.typestate_canCastTo(type);
  }

  @Override
  public EvaluateResult castTo(EvaluationContext context, Location location, AlligatorusType type) {
    if (!(type instanceof MortarDataType)) {
      throw new Assertion();
    }
    return typestate.typestate_varCastTo(context, location, (MortarDataType) type);
  }

  @Override
  public Value unfork(EvaluationContext context, Location location, ROPair<Location, Value> other) {
    if (!(other.second instanceof MortarDataValue)) {
      context.errors.add(
          new GeneralLocationError(
              other.first, "Type doesn't match other branches")); // todo log both locations
      return ErrorValue.value;
    }
    return new MortarDataValueVariableStack(
        this.typestate.typestate_unfork(
            context, location, ((MortarDataValue) other.second).typestate, other.first));
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    return EvaluateResult.pure(this);
  }
}
