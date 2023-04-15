package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.model.error.SetNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarMethodValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarMethodValueVariableDeferred;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarObjectMethodAll implements MortarObjectField, MortarObjectFieldstate {
  private final StaticAutogen.FuncInfo funcInfo;
  private final MortarObjectInnerType parentType;

  public MortarObjectMethodAll(MortarObjectInnerType parentType, StaticAutogen.FuncInfo funcInfo) {
    this.funcInfo = funcInfo;
    this.parentType = parentType;
  }

  @Override
  public MortarObjectFieldstate field_newFieldstate() {
    return this;
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
    return EvaluateResult.pure(new MortarMethodValueVariableDeferred(funcInfo, base));
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(new MortarMethodValueConst(funcInfo, base));
  }

  @Override
  public EvaluateResult fieldstate_variableValueAccess(
      EvaluationContext context, Location location, Value field) {
    context.errors.add(new SetNotSupported(location));
    return EvaluateResult.error;
  }

  public EvaluateResult fieldstate_set(
      EvaluationContext context, Location location, JavaBytecode base, Value value) {
    context.errors.add(new SetNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public boolean fieldstate_canCastTo(AlligatorusType prototype) {
    return false;
  }

  @Override
  public boolean fieldstate_triviallyAssignableTo(MortarObjectField field) {
    return false;
  }

  @Override
  public MortarObjectFieldstate fieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation) {
    throw new Assertion(); // Can't be returned
  }

  @Override
  public boolean fieldstate_varBindMerge(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation) {
    throw new Assertion(); // Can't be bound
  }
}
