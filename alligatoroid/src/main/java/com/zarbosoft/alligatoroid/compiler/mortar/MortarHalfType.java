package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.Value;

public interface MortarHalfType extends SimpleValue {
  Value asValue(MortarProtocode lower);

  default EvaluateResult valueAccess(
          EvaluationContext context, Location location, Value field, MortarProtocode lower) {
    context.moduleContext.log.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }
}
