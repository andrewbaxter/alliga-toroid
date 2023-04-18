package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.rendaw.common.ROPair;

public interface MortarTupleFieldstate {
  EvaluateResult tuplefieldstate_constAsValue(
      EvaluationContext context, Location location, Object base, int key);

  MortarTupleFieldstate tuplefieldstate_fork();

  JavaDataDescriptor tuplefieldstate_jvmDesc();

  EvaluateResult tuplefieldstate_variableAsValue(
      EvaluationContext context, Location location, MortarDeferredCode baseCode, int field);

  boolean tuplefieldstate_canCastTo(MortarTupleFieldstate other);

  MortarTupleField tuplefieldstate_asType();

  boolean tuplefieldstate_triviallyAssignableTo(MortarTupleFieldstate other);

  boolean tuplefieldstate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarTupleFieldstate other,
      Location otherLocation);

  MortarTupleFieldstate tuplefieldstate_unfork(
      EvaluationContext context,
      Location location,
      ROPair<Object, MortarTupleFieldstate> other,
      Location otherLocation);

    Object tuplefieldstate_constCastTo(EvaluationContext context, Location location, AlligatorusType other, Object value);
}
