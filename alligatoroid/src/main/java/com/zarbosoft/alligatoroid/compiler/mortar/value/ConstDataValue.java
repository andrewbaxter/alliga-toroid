package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.IdentityExportableType;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialRecord;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialString;
import com.zarbosoft.alligatoroid.compiler.inout.graph.SemiserialSubvalue;
import com.zarbosoft.alligatoroid.compiler.inout.graph.Semiserializer;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.ConstBinding;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.graph.ConstExportType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSOrderedMap;

public class ConstDataValue implements DataValue, IdentityExportable {
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
  public IdentityExportableType exportableType() {
    return ConstExportType.exportType;
  }

  @Override
  public SemiserialSubvalue graphSemiserializeBody(
      long importCacheId,
      Semiserializer semiserializer,
      ROList<Exportable> path,
      ROList<String> accessPath) {
    final MortarDataType mortarType = mortarType();
    return SemiserialRecord.create(
        new TSOrderedMap<SemiserialSubvalue, SemiserialSubvalue>(
            s ->
                s.putNew(
                        SemiserialString.create(ConstExportType.KEY_TYPE),
                        mortarType.graphSemiserialize(
                            importCacheId, semiserializer, path, accessPath))
                    .putNew(
                        SemiserialString.create(ConstExportType.KEY_VALUE),
                        mortarType.graphSemiserializeValue(
                            getInner(), importCacheId, semiserializer, path, accessPath))));
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
