package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.ROPair;

public class VariableDataValue implements  DataValue, NoExportValue  {
  public final MortarDeferredCode code;
  public final MortarDataType type;

  public VariableDataValue(MortarDataType type, MortarDeferredCode code) {
    this.code = code;
    this.type = type;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.varValueBind(context, code);
  }

  @Override
  public MortarDataType mortarType() {
    return type;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return code.drop(context, location);
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location location) {
    return EvaluateResult.pure(this);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return type.variableValueAccess(context, location, code, field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    com.zarbosoft.alligatoroid.compiler.ThreadEvaluationContext.addError(new CantSetStackValue(location));
    return EvaluateResult.error;
  }
}
