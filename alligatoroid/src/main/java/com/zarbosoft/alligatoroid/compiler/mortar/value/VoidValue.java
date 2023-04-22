package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.VoidType;
import com.zarbosoft.alligatoroid.compiler.mortar.VoidTypestate;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class VoidValue implements Value, NoExportValue {
  private final VoidTypestate typestate;

  public VoidValue(VoidTypestate typestate) {
    this.typestate = typestate;
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
  return typestate.typestate_traceFields(context,location);
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, new SimpleBinding(this));
  }

  @Override
  public AlligatorusType type(EvaluationContext context) {
    return typestate.typestate_asType();
  }

  @Override
  public MortarTargetCode consume(EvaluationContext context, Location location) {
    return MortarTargetCode.empty;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return typestate.typestate_varAccess(context, location, field);
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
    if (!(type instanceof VoidType)) {
      throw new Assertion();
    }
    return typestate.typestate_castTo(context, location, (VoidType) type);
  }

  @Override
  public Value unfork(EvaluationContext context, Location location, ROPair<Location, Value> other) {
    if (!(other.second instanceof VoidValue)) {
      context.errors.add(
          new GeneralLocationError(
              other.first, "Type doesn't match other branches")); // todo log both locations
      return ErrorValue.value;
    }
    return new VoidValue(
        this.typestate.typestate_unfork(
            context, location, ((VoidValue) other.second).typestate, other.first));
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    return EvaluateResult.pure(this);
  }
}
