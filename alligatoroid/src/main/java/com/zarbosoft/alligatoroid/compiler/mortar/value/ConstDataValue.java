package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.ExportableType;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.ValueNotWhole;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.ConstBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.ConstExportType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.ROPair;

import java.util.function.Consumer;

import static com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataBuiltinSingletonValue.nullValue;

public abstract class ConstDataValue implements DataValue {
  static EvaluateResult setHelper(
      ConstDataValue self,
      EvaluationContext context,
      Location location,
      Value value,
      Consumer<Object> setInner) {
    if (!self.mortarType().assertAssignableFrom(context, location, value))
      return EvaluateResult.error;
    if (value instanceof VariableDataValue) {
      context.moduleContext.errors.add(new ValueNotWhole(location));
      return EvaluateResult.error;
    }
    setInner.accept(((ConstDataValue) value).getInner());
    return EvaluateResult.pure(nullValue);
  }

  @Override
  public ExportableType graphType() {
    return ConstExportType.exportType;
  }

  @Override
  public EvaluateResult export(EvaluationContext context, Location location) {
    return EvaluateResult.pure(this);
  }

  @Override
  public final TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  public abstract Object getInner();

  @Override
  public abstract MortarDataType mortarType();

  @Override
  public final EvaluateResult vary(EvaluationContext context, Location location) {
    if (!MortarTargetModuleContext.assertTarget(context, location)) return EvaluateResult.error;
    return EvaluateResult.pure(
        mortarType().stackAsValue(mortarType().constValueVary(context, getInner())));
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
  public final ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return new ROPair<>(null, new ConstBinding(mortarType(), getInner()));
  }
}
