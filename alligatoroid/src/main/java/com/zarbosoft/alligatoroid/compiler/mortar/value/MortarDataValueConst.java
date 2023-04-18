package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataTypestate;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarDataValueConst extends MortarDataValue implements BuiltinAutoExportable {
  public final Object value;

  public MortarDataValueConst(MortarDataTypestate typestate, Object value) {
    super(typestate);
    this.value = value;
  }

  public static MortarDataValueConst create(MortarDataTypestate typestate, Object value) {
    final MortarDataValueConst out = new MortarDataValueConst(typestate, value);
    out.postInit();
    return out;
  }

  @Override
  public EvaluateResult export(EvaluationContext context, Location location) {
    return EvaluateResult.pure(this);
  }

  @Override
  public MortarTargetCode consume(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public final TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult call(EvaluationContext context, Location location, Value argument) {
    return typestate.typestate_constCall(context, location, getInner(), argument);
  }

  @Override
  public final EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return typestate.typestate_constValueAccess(context, location, getInner(), field);
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
    return typestate.typestate_traceFields(context, location, getInner());
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location id) {
    return typestate.typestate_constVary(context, id, value);
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
    return typestate.typestate_constCastTo(context, location, (MortarDataType) type, value);
  }

  @Override
  public Value unfork(EvaluationContext context, Location location, ROPair<Location, Value> other) {
    throw new Assertion(); // Should be varied/realized before reaching here
  }

  @Override
  public EvaluateResult realize(EvaluationContext context, Location id) {
    return vary(context, id);
  }

  @Override
  public final ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return typestate.typestate_constBind(context, location, getInner());
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    context.errors.add(new CantSetStackValue(location));
    return EvaluateResult.error;
  }

  public Object getInner() {
    return value;
  }

  public MortarDataType type() {
    return typestate.typestate_asType();
  }
}
