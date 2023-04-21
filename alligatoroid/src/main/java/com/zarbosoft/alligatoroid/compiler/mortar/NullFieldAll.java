package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import org.jetbrains.annotations.NotNull;

public class NullFieldAll
    implements MortarObjectField,
        MortarObjectFieldstate,
        MortarRecordField,
        MortarRecordFieldstate,
        BuiltinAutoExportable {
  public static final NullFieldAll inst = new NullFieldAll();

  private NullFieldAll() {}

  @Override
  public MortarObjectFieldstate field_newFieldstate() {
    return this;
  }

  @Override
  public AlligatorusType field_asType() {
    return NullType.type;
  }

  @Override
  public MortarObjectFieldstate fieldstate_fork() {
    return this;
  }

  @Override
  public MortarObjectField fieldstate_asField() {
    return this;
  }

  @Override
  public EvaluateResult fieldstate_variableObjectFieldAsValue(
      EvaluationContext context, Location location, MortarDeferredCode base) {
    return variableAsValue(base);
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(NullValue.value);
  }

  @Override
  public boolean fieldstate_canCastTo(AlligatorusType type) {
    return canCastTo(type);
  }

  @Override
  public boolean fieldstate_triviallyAssignableTo(MortarObjectField field) {
    return field == this;
  }

  @Override
  public MortarObjectFieldstate fieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation) {
    if (other != this) {
      return null;
    }
    return this;
  }

  @Override
  public boolean fieldstate_varBindMerge(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation) {
    return true;
  }

  @Override
  public MortarRecordFieldstate recordfield_newFieldstate() {
    return this;
  }

  @Override
  public AlligatorusType recordfield_asType() {
    return NullType.type;
  }

  @Override
  public EvaluateResult recordfieldstate_constAsValue(
      EvaluationContext context, Location location, Object base, int key) {
    return EvaluateResult.pure(NullValue.value);
  }

  @Override
  public MortarRecordFieldstate recordfieldstate_fork() {
    return this;
  }

  @Override
  public EvaluateResult recordfieldstate_variableAsValue(
      EvaluationContext context, Location location, MortarDeferredCode baseCode, int field) {
    return variableAsValue(baseCode);
  }

  @NotNull
  private static EvaluateResult variableAsValue(MortarDeferredCode baseCode) {
    return EvaluateResult.simple(NullValue.value, new MortarTargetCode(baseCode.drop()));
  }

  @Override
  public boolean recordfieldstate_canCastTo(AlligatorusType other) {
    return canCastTo(other);
  }

  private static boolean canCastTo(AlligatorusType other) {
    return other == NullType.type;
  }

  @Override
  public MortarRecordField recordfieldstate_asField() {
    return this;
  }

  @Override
  public boolean recordfieldstate_triviallyAssignableTo(MortarRecordFieldstate other) {
    return other == this;
  }

  @Override
  public boolean recordfieldstate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation) {
    return true;
  }

  @Override
  public MortarRecordFieldstate recordfieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation) {
    if (other != this) {
      return null;
    }
    return this;
  }

  @Override
  public Object recordfieldstate_constCastTo(
      EvaluationContext context, Location location, AlligatorusType other, Object value) {
    return null;
  }

  @Override
  public AlligatorusType recordfieldstate_asType() {
    return NullType.type;
  }
}
