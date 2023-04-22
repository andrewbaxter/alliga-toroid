package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessRecordField;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeStack;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueConst;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataValueVariableDeferred;

public class MortarDataGenericRecordFieldstate implements MortarRecordFieldstate {
  private final int offset;
  private final MortarDataTypestateForGeneric typestate;

  public MortarDataGenericRecordFieldstate(int offset, MortarDataTypestateForGeneric typestate) {
    this.offset = offset;
    this.typestate = typestate;
  }

  @Override
  public EvaluateResult recordfieldstate_constAsValue(
          EvaluationContext context, Location location, Object[] data) {
    return EvaluateResult.pure(
        new MortarDataValueConst(typestate.typestate_fork(), data[offset]));
  }

  @Override
  public MortarRecordFieldstate recordfieldstate_fork() {
    return new MortarDataGenericRecordFieldstate(offset, typestate.typestate_fork());
  }

  @Override
  public EvaluateResult recordfieldstate_variableAsValue(
      EvaluationContext context, Location location, MortarDeferredCode baseCode) {
    return EvaluateResult.pure(
        new MortarDataValueVariableDeferred(
            typestate.typestate_fork(),
            new MortarDeferredCodeAccessRecordField(
                new MortarDeferredCodeStack(),
                offset,
                typestate.typestate_jvmToObj(),
                typestate.typestate_jvmFromObj())));
  }

  @Override
  public boolean recordfieldstate_canCastTo(AlligatorusType other) {
    return typestate.typestate_canCastTo(other);
  }

  @Override
  public MortarRecordField recordfieldstate_asField() {
    return new MortarDataGenericRecordField(offset, typestate.typestate_asType());
  }

  @Override
  public boolean recordfieldstate_triviallyAssignableTo(MortarRecordFieldstate other) {
    return typestate.typestate_triviallyAssignableTo(other.recordfieldstate_asType());
  }

  @Override
  public boolean recordfieldstate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation) {
    return typestate.typestate_bindMerge(
        context, location, ((MortarDataGenericRecordFieldstate) other).typestate, otherLocation);
  }

  @Override
  public MortarRecordFieldstate recordfieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation) {
    return new MortarDataGenericRecordFieldstate(
        offset,
        typestate.typestate_unfork(
            context,
            location,
            ((MortarDataGenericRecordFieldstate) other).typestate,
            otherLocation));
  }

  @Override
  public Object recordfieldstate_constCastTo(
      EvaluationContext context, Location location, AlligatorusType other, Object value) {
    return typestate.typestate_constCastTo(context, location, (MortarType) other, value);
  }

  @Override
  public AlligatorusType recordfieldstate_asType() {
    return typestate.typestate_asType();
  }
}
