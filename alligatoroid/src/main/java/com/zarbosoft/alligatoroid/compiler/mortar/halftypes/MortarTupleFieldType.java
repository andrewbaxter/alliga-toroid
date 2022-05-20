package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.TSList;

public interface MortarTupleFieldType {
  EvaluateResult constTupleFieldAsValue(
      EvaluationContext context, Location location, Object base, int key);

  MortarTupleFieldType tupleFieldFork();

  JavaDataDescriptor jvmDesc();

  EvaluateResult variableTupleFieldAsValue(
          EvaluationContext context,
          Location location,
          MortarDeferredCode baseCode,
          int field);

  boolean tupleAssignmentCheckFieldAssignableFrom(
      TSList<Error> errors,
      Location location,
      MortarTupleFieldType otherField,
      TSList<Object> path);
}
