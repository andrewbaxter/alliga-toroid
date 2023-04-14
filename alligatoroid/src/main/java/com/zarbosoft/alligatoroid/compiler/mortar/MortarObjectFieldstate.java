package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.model.Binding;
import com.zarbosoft.alligatoroid.compiler.model.error.AccessNotSupported;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.ROPair;

public interface MortarObjectFieldstate {
  MortarObjectFieldstate fieldstate_fork();

  MortarObjectField fieldstate_asField();

  EvaluateResult fieldstate_variableObjectFieldAsValue(
      EvaluationContext context, Location location, MortarDeferredCode base);

  EvaluateResult fieldstate_constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base);

  default EvaluateResult fieldstate_variableValueAccess(
      EvaluationContext context, Location location, Value field) {
    context.errors.add(new AccessNotSupported(location));
    return EvaluateResult.error;
  }

  boolean fieldstate_canCastTo(AlligatorusType prototype);

  boolean fieldstate_triviallyAssignableTo(MortarObjectField field);

  MortarObjectFieldstate fieldstate_unfork(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation);

  boolean fieldstate_varBindMerge(
      EvaluationContext context,
      Location location,
      MortarObjectFieldstate other,
      Location otherLocation);
}
