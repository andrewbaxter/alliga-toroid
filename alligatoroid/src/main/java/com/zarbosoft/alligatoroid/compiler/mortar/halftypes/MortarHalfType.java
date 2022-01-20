package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;

public interface MortarHalfType {
  MortarValue asValue(Location location, MortarProtocode lower);

  default EvaluateResult valueAccess(
          EvaluationContext context, Location location, MortarValue field, MortarProtocode lower) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }
}
