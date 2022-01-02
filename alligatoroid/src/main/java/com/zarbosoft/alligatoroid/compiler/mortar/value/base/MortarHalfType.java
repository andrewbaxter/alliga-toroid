package com.zarbosoft.alligatoroid.compiler.mortar.value.base;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarProtocode;

public interface MortarHalfType extends SimpleValue, LeafValue, AutoGraphMixin {
  Value asValue(MortarProtocode lower);

  default EvaluateResult valueAccess(
      EvaluationContext context, Location location, Value field, MortarProtocode lower) {
    context.moduleContext.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }
}
