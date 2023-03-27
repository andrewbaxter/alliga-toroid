package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarCastable;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataPrototype;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarDataVariableValue implements DataValue, NoExportValue, MortarCastable {
  public final MortarDeferredCode code;
  public final MortarDataType type;

  public MortarDataVariableValue(MortarDataType type, MortarDeferredCode code) {
    this.code = code;
    this.type = type;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.type_varValueBind(context, code.consume());
  }

  @Override
  public MortarDataType mortarType() {
    return type;
  }

  @Override
  public MortarTargetCode consume(EvaluationContext context, Location location) {
    return new MortarTargetCode(code.consume());
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return new MortarTargetCode(code.drop());
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return type.type_variableValueAccess(context, location, code, field);
  }

  @Override
  public EvaluateResult set(EvaluationContext context, Location location, Value value) {
    context.errors.add(new CantSetStackValue(location));
    return EvaluateResult.error;
  }

  @Override
  public EvaluateResult vary(EvaluationContext context, Location id) {
    return EvaluateResult.pure(this);
  }

  @Override
  public JavaBytecode castTo(MortarDataPrototype prototype) {
    return type.type_castTo(prototype, code);
  }

  @Override
  public boolean canCastTo(MortarDataPrototype prototype) {
    return type.type_canCastTo(prototype);
  }
}
