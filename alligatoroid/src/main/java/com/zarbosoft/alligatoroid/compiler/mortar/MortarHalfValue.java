package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.OkValue;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.rendaw.common.ROPair;

public class MortarHalfValue implements OkValue {
  public final MortarProtocode lower;
  public final MortarHalfDataType type;

  public MortarHalfValue(MortarHalfDataType type, MortarProtocode lower) {
    this.type = type;
    this.lower = lower;
  }

  @Override
  public TargetCode drop(EvaluationContext context, Location location) {
    return lower.drop(context, location);
  }

  @Override
  public EvaluateResult access(EvaluationContext context, Location location, Value field) {
    return type.valueAccess(context, location, field, lower);
  }

  @Override
  public ROPair<TargetCode, Binding> bind(EvaluationContext context, Location location) {
    return type.valueBind(lower);
  }

  public MortarCode lower() {
    return lower.lower();
  }
}
