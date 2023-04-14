package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessObjectField;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableDeferred;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class MortarPrimitiveFieldAll implements MortarObjectField, MortarObjectFieldstate {
  private final MortarObjectInnerType parentType;
  private final String name;
  public final MortarPrimitiveAll data;

  public MortarPrimitiveFieldAll(
      MortarObjectInnerType parentType, String name, MortarPrimitiveAll data) {
    this.parentType = parentType;
    this.name = name;
    this.data = data;
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
  public MortarObjectFieldstate field_newFieldstate() {
    return this;
  }

  @Override
  public EvaluateResult fieldstate_variableObjectFieldAsValue(
      EvaluationContext context, Location location, MortarDeferredCode base) {
    return EvaluateResult.pure(
        new MortarDataValueVariableDeferred(
            data,
            new MortarDeferredCodeAccessObjectField(
                base, parentType.name.asInternalName(), name, data.inner.jvmDesc())));
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(
        MortarDataValueConst.create(data, uncheck(() -> base.getClass().getField(name).get(base))));
  }

  @Override
  public EvaluateResult fieldstate_variableValueAccess(
      EvaluationContext context, Location location, Value field) {
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  @Override
  public boolean fieldstate_canCastTo(AlligatorusType type) {
    return data.triviallyAssignableTo(type);
  }

  @Override
  public boolean fieldstate_triviallyAssignableTo(MortarObjectField field) {
    if (!(field instanceof MortarPrimitiveFieldAll)) {
      return false;
    }
    return data.triviallyAssignableTo(((MortarPrimitiveFieldAll) field).data);
  }

  @Override
  public MortarObjectFieldstate fieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation) {
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
