package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarObjectFieldstate;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarObjectFieldstateData;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarDataValueVariableField implements MortarDataValue, NoExportValue {
  public final MortarObjectFieldstateData fieldstate;
  public final MortarDeferredCode parentCode;

  public MortarDataValueVariableField(
          MortarObjectFieldstateData fieldstate, MortarDeferredCode parentCode) {
    this.fieldstate = fieldstate;
    this.parentCode = parentCode;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return fieldstate.fieldstate_bind(context, location, parentCode);
  }

  @Override
  public MortarDataType type() {
    return fieldstate.fieldstate_asType();
  }

  @Override
  public MortarTargetCode consume(EvaluationContext context, Location location) {
    return new MortarTargetCode(fieldstate.fieldstate_consume(context, location, parentCode));
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return null;
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return fieldstate.fieldstate_variableValueAccess(context, location, field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    return fieldstate.fieldstate_set(
        context, location, fieldstate.fieldstate_consume(context, location, parentCode), value);
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location id) {
    return EvaluateResult.pure(this);
  }

  @Override
  public boolean canCastTo(AlligatorusType type) {
    return fieldstate.fieldstate_canCastTo(type);
  }

  @Override
  public EvaluateResult castTo(EvaluationContext context, Location location, AlligatorusType type) {
    if (!(type instanceof MortarDataType)) {
      throw new Assertion();
    }
    return EvaluateResult.pure(
        ((MortarDataType) type)
            .type_stackAsValue(
                fieldstate.fieldstate_castTo(
                    context, location, (MortarDataType) type, parentCode)));
  }
}
