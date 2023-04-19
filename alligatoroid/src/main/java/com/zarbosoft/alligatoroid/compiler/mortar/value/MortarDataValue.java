package com.zarbosoft.alligatoroid.compiler.mortar.value;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataTypestate;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetCode;

public abstract class MortarDataValue implements Value {
  public final MortarDataTypestate typestate;

  protected MortarDataValue(MortarDataTypestate typestate) {
    this.typestate = typestate;
  }

  @Override
  public abstract MortarTargetCode consume(EvaluationContext context, Location location);
}
