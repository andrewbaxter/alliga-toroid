package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.TSList;

public interface MortarTupleFieldType {
  EvaluateResult tuple_fieldtype_constAsValue(
      EvaluationContext context, Location location, Object base, int key);

  MortarTupleFieldType tuple_fieldtype_fork();

  JavaDataDescriptor tuple_fieldtype_jvmDesc();

  EvaluateResult tuple_fieldtype_variableAsValue(
          EvaluationContext context,
          Location location,
          MortarDeferredCode baseCode,
          int field);
}
