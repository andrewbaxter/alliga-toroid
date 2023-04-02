package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.CantSetStackValue;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.GeneralLocationError;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataTypestate;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarDataValueVariableStack implements MortarDataValue, NoExportValue {
  public final MortarDeferredCode code;
  public final MortarDataTypestate typestate;

  public MortarDataValueVariableStack(MortarDataTypestate typestate, MortarDeferredCode code) {
    this.code = code;
    this.typestate = typestate;
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return typestate.typestate_varValueBind(context, code.consume());
  }

  @Override
  public MortarDataType type() {
    return typestate.typestate_asType();
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
    return typestate.typestate_variableValueAccess(context, location, code, field);
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
  public boolean canCastTo(AlligatorusType type) {
    return typestate.typestate_canCastTo( type);
  }

  @Override
  public EvaluateResult castTo(EvaluationContext context, Location location, AlligatorusType type) {
    if (!(type instanceof MortarDataType)) {
      throw new Assertion();
    }
    return EvaluateResult.pure(
        ((MortarDataType) type)
            .type_stackAsValue(
                typestate.typestate_castTo(context, location, (MortarDataType) type, code)));
  }
}
