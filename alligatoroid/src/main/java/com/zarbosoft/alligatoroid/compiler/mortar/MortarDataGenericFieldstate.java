package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessObjectField;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableDeferred;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarMethodValueVariableDeferred;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class MortarDataGenericFieldstate implements MortarObjectFieldstate {
  private final MortarObjectInnerType parentInfo;
  private final String name;
  private final MortarDataTypestate typestate;

  public MortarDataGenericFieldstate(
      MortarObjectInnerType parentInfo, String name, MortarDataTypestate typestate) {
    this.parentInfo = parentInfo;
    this.name = name;
    this.typestate = typestate;
  }

  @Override
  public MortarObjectFieldstate fieldstate_fork() {
    return new MortarDataGenericFieldstate(parentInfo, name, typestate.typestate_fork());
  }

  @Override
  public MortarObjectField fieldstate_asField() {
    return new MortarDataGenericField(parentInfo, name, typestate.typestate_asType());
  }

  @Override
  public EvaluateResult fieldstate_variableObjectFieldAsValue(
      EvaluationContext context, Location location, MortarDeferredCode base) {
    return EvaluateResult.pure(
        new MortarDataValueVariableDeferred(
            typestate.typestate_fork(),
            new MortarDeferredCodeAccessObjectField(
                base, parentInfo.name.asInternalName(), name, typestate.typestate_jvmDesc())));
  }

  @Override
  public EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(
        MortarDataValueConst.create(
            typestate.typestate_fork(), uncheck(() -> base.getClass().getField(name).get(base))));
  }

  @Override
  public boolean fieldstate_canCastTo(AlligatorusType type) {
    return typestate.typestate_canCastTo(type);
  }

  @Override
  public boolean fieldstate_triviallyAssignableTo(MortarObjectField field) {
    if (!(field instanceof MortarDataGenericField)) {
      return false;
    }
    return typestate.typestate_triviallyAssignableTo(((MortarDataGenericField) field).type);
  }

  @Override
  public MortarObjectFieldstate fieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation) {
    return new MortarDataGenericFieldstate(
        parentInfo,
        name,
        typestate.typestate_unfork(
            context, location, ((MortarDataGenericFieldstate) other).typestate, otherLocation));
  }

  @Override
  public boolean fieldstate_varBindMerge(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation) {
    return typestate.typestate_bindMerge(
        context, location, ((MortarDataGenericFieldstate) other).typestate, otherLocation);
  }
}
