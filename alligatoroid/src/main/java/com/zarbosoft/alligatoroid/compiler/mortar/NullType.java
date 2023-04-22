package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.inout.graph.AutoExportable;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VoidValue;

public class NullType extends VoidTypeSimple implements AutoExportable {
  public static final NullType INST = new NullType();

  @Override
  public boolean recordfieldstate_canCastTo(AlligatorusType other) {
    return other == this;
  }

  @Override
  public boolean recordfieldstate_triviallyAssignableTo(MortarRecordFieldstate other) {
    return other == this;
  }

  @Override
  public boolean recordfieldstate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation) {
    return true;
  }

  @Override
  public MortarRecordFieldstate recordfieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation) {
    if (other != this) {
      context.errors.add(new GeneralLocationError(location, "Unfork type mismatch"));
    }
    return this;
  }

  @Override
  public boolean typestate_canCastTo(AlligatorusType other) {
    return other == this;
  }

  @Override
  public EvaluateResult typestate_castTo(
      EvaluationContext context, Location location, VoidType other) {
    return EvaluateResult.pure(new VoidValue(this));
  }

  @Override
  public VoidTypestate typestate_unfork(
      EvaluationContext context, Location location, VoidTypestate other, Location otherLocation) {
    if (other != this) {
      context.errors.add(new GeneralLocationError(location, "Unfork type mismatch"));
    }
    return this;
  }
}
