package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.TargetCode;

public interface MortarProtocode {
  public MortarCode lower();

  TargetCode drop(EvaluationContext context, Location location);
}
