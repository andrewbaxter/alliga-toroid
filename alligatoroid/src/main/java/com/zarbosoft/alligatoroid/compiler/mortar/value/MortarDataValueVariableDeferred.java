package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataTypestate;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.NullValue;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarDataValueVariableDeferred extends MortarDataValue implements NoExportValue {
  public final MortarDeferredCode code;

  public MortarDataValueVariableDeferred(MortarDataTypestate typestate, MortarDeferredCode code) {
    super(typestate);
    this.code = code;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    final ROPair<JavaBytecode, Binding> binding = typestate.typestate_varBind(context);
    return new ROPair<>(
        new MortarTargetCode(new JavaBytecodeSequence().add(code.consume()).add(binding.first)),
        binding.second);
  }

  @Override
  public MortarDataType type(EvaluationContext context) {
    return typestate.typestate_asType();
  }

  @Override
  public TargetCode cleanup(EvaluationContext context, Location location) {
    return new MortarTargetCode(typestate.typestate_cleanup(context, location));
  }

  @Override
  public MortarTargetCode consume(EvaluationContext context, Location location) {
    return new MortarTargetCode(code.consume());
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return new MortarTargetCode(code.drop());
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value type) {
    return typestate.typestate_varAccess(context, location, type, code);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    final MortarDataType currentType = this.typestate.typestate_asType();
    if (!value.canCastTo(context, currentType)) {
      context.errors.add(new GeneralLocationError(location, "RHS can't be cast to LHS"));
      return EvaluateResult.error;
    }
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    final Value usedValue =
        ectx.record(
            ectx.record(value.castTo(context, location, currentType)).vary(context, location));
    ectx.recordEffect(usedValue.consume(context, location));
    ectx.recordEffect(usedValue.cleanup(context, location));
    return EvaluateResult.simple(
        NullValue.value,
        new MortarTargetCode(code.set(MortarTargetCode.ex(ectx.build(null).effect))));
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
    final EvaluateResult.Context ectx = new EvaluateResult.Context(context, location);
    ectx.recordEffect(new MortarTargetCode(code.consume()));
    return ectx.build(
        ectx.record(typestate.typestate_varCastTo(context, location, (MortarDataType) type)));
  }

  @Override
  public Value unfork(EvaluationContext context, Location location, ROPair<Location, Value> other) {
    // Should already be realized as stack values before unforking
    throw new Assertion();
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    return EvaluateResult.simple(
        new MortarDataValueVariableStack(typestate), new MortarTargetCode(code.consume()));
  }
}
