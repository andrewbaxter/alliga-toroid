package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.ROPair;

public class NullMortarField implements MortarObjectField, MortarObjectFieldstate {
  public static final NullMortarField field = new NullMortarField();

  private NullMortarField() {}

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
    return new EvaluateResult(new MortarTargetCode(base.drop()), null, NullValue.value, jumpValues, jumpValues);
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(NullValue.value);
  }

  @Override
  public ROPair<TargetCode, Binding> fieldstate_bind(
      EvaluationContext context, Location location, MortarDeferredCode parentCode) {
    return new ROPair<>(null, NullBinding.binding);
  }

  @Override
  public EvaluateResult fieldstate_set(
      EvaluationContext context, Location location, JavaBytecode base, Value value) {
    return new EvaluateResult(value.drop(context, location), null, NullValue.value, jumpValues, jumpValues);
  }

  @Override
  public JavaBytecode fieldstate_castTo(
      EvaluationContext context,
      Location location,
      MortarDataType prototype,
      MortarDeferredCode parentCode) {
    return parentCode.drop();
  }

  @Override
  public boolean fieldstate_canCastTo(AlligatorusType type) {
    return type == NullType.type;
  }
}
