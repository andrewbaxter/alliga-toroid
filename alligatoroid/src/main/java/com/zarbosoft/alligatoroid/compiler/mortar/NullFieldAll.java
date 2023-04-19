package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;

public class NullFieldAll implements MortarObjectField, MortarObjectFieldstate {
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
    return EvaluateResult.simple(NullValue.value, new MortarTargetCode(base.drop()));
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(NullValue.value);
  }

  @Override
  public boolean fieldstate_canCastTo(AlligatorusType type) {
    return type == NullType.type;
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
}
