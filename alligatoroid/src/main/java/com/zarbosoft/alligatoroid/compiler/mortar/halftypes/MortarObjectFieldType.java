package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;

public interface MortarObjectFieldType {
  EvaluateResult constObjectFieldAsValue(EvaluationContext context, Location location, Object base);

  MortarObjectFieldType objectFieldFork();

  JavaDataDescriptor jvmDesc();

  EvaluateResult variableObjectFieldAsValue(
          EvaluationContext context,
          Location location,
          MortarDeferredCode baseCode);
}
