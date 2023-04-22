package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;

public interface VoidTypestate {
  AlligatorusType typestate_asType();

  EvaluateResult typestate_varAccess(EvaluationContext context, Location location, Value field);

  boolean typestate_canCastTo(AlligatorusType other);

  EvaluateResult typestate_castTo(EvaluationContext context, Location location, VoidType other);

  VoidTypestate typestate_unfork(
      EvaluationContext context, Location location, VoidTypestate other, Location otherLocation);

  default ROList<String> typestate_traceFields(EvaluationContext context, Location location) {
    return ROList.empty;
  }
}
