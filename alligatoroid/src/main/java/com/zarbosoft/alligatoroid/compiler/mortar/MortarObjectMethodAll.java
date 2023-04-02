package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.SetNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarFunctionValueConstField;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarFunctionValueVariableField;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

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
    return EvaluateResult.pure(new MortarFunctionValueVariableField(funcInfo, base));
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(new MortarFunctionValueConstField(funcInfo, base));
  }

  @Override
  public ROPair<TargetCode, Binding> fieldstate_bind(
      EvaluationContext context, Location location, MortarDeferredCode parentCode) {
    return new ROPair<>(null, ErrorValue.binding);
  }

  @Override
  public EvaluateResult fieldstate_variableValueAccess(
      EvaluationContext context, Location location, Value field) {
    context.errors.add(new SetNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult fieldstate_set(
      EvaluationContext context, Location location, JavaBytecode base, Value value) {
    context.errors.add(new SetNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public JavaBytecode fieldstate_castTo(
      EvaluationContext context,
      Location location,
      MortarDataType prototype,
      MortarDeferredCode parentCode) {
    throw new Assertion();
  }

  @Override
  public boolean fieldstate_canCastTo(AlligatorusType prototype) {
    return false;
  }

  @Override
  public boolean fieldstate_triviallyAssignableTo(MortarObjectField field) {
  return false;
  }
}
