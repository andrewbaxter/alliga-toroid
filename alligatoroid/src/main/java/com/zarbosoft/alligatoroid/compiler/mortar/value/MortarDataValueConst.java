package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.ConstBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataTypestate;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarDataValueConst implements MortarDataValue, BuiltinAutoExportable {
  public MortarDataTypestate typestate;
  public Object value;

  public static MortarDataValueConst create(MortarDataTypestate typestate, Object value) {
    final MortarDataValueConst out = new MortarDataValueConst();
    out.typestate = typestate;
    out.value = value;
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
    return typestate.typestate_vary(context, id, value);
  }

  @Override
  public final ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, new ConstBinding(typestate, getInner()));
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
