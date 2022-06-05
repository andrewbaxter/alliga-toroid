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
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class ConstDataValue implements DataValue, BuiltinAutoExportable {
  public static final ConstDataValue nullValue = create(MortarNullType.type, null);
  public MortarDataType type;
  public Object value;

  public static ConstDataValue create(MortarDataType type, Object value) {
    final ConstDataValue out = new ConstDataValue();
    out.type = type;
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
    return mortarType().constCall(context, location, getInner(), argument);
  }

  @Override
  public final EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return mortarType().constValueAccess(context, location, getInner(), field);
  }

  @Override
  public ROList<String> traceFields(EvaluationContext context, Location location) {
    return mortarType().traceFields(context, location, getInner());
  }

  @Override
  public final ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, new ConstBinding(mortarType(), getInner()));
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    context.errors.add(new CantSetStackValue(location));
    return EvaluateResult.error;
  }

  public Object getInner() {
    return value;
  }

  public MortarDataType mortarType() {
    return type;
  }
}
