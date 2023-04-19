package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;

public interface MortarRecordFieldstate {
  EvaluateResult recordfieldstate_constAsValue(
      EvaluationContext context, Location location, Object base, int key);

  MortarRecordFieldstate recordfieldstate_fork();

    EvaluateResult recordfieldstate_variableAsValue(
      EvaluationContext context, Location location, MortarDeferredCode baseCode, int field);

  boolean recordfieldstate_canCastTo(AlligatorusType other);

  MortarRecordField recordfieldstate_asField();

  boolean recordfieldstate_triviallyAssignableTo(MortarRecordFieldstate other);

  boolean recordfieldstate_bindMerge(
      EvaluationContext context,
      Location location,
      MortarRecordFieldstate other,
      Location otherLocation);

  MortarRecordFieldstate recordfieldstate_unfork(
          EvaluationContext context,
          Location location,
          MortarRecordFieldstate other,
          Location otherLocation);

    Object recordfieldstate_constCastTo(EvaluationContext context, Location location, AlligatorusType other, Object value);

  AlligatorusType recordfieldstate_asType();
}
