package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.Field;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessObjectField;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class MortarDataField implements Field {
  private final MortarDataType mortarDataType;
  private final ObjectMeta baseMeta;
  private final String name;

  public MortarDataField(MortarDataType mortarDataType, ObjectMeta baseMeta, String name) {
    this.mortarDataType = mortarDataType;
    this.baseMeta = baseMeta;
    this.name = name;
  }

  @Override
  public EvaluateResult constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base) {
    return EvaluateResult.pure(
        ConstDataValue.create(
            mortarDataType, uncheck(() -> base.getClass().getField(name).get(base))));
  }

  @Override
  public Field objectFieldFork() {
    return this;
  }

  @Override
  public JavaDataDescriptor jvmDesc() {
    return mortarDataType.jvmDesc();
  }

  @Override
  public EvaluateResult variableObjectFieldAsValue(
      EvaluationContext context, Location location, MortarDeferredCode baseCode) {
    return EvaluateResult.pure(
        new VariableDataValue(
            mortarDataType,
            new MortarDeferredCodeAccessObjectField(
                baseCode, baseMeta.name.asInternalName(), name, jvmDesc())));
  }
}
