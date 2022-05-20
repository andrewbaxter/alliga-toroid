package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessObjectField;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCodeAccessTupleField;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarObjectFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarTupleFieldType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataValue;
import com.zarbosoft.rendaw.common.TSList;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public interface MortarSimpleDataType
    extends MortarObjectFieldType, MortarTupleFieldType, MortarDataType {
  @Override
  default EvaluateResult variableObjectFieldAsValue(
      EvaluationContext context,
      Location location,
      MortarDeferredCode baseCode,
      JavaInternalName baseName,
      String fieldName) {
    return EvaluateResult.pure(
        new VariableDataValue(
            this,
            new MortarDeferredCodeAccessObjectField(baseCode, baseName, fieldName, jvmDesc())));
  }

  @Override
  default EvaluateResult constTupleFieldAsValue(
      EvaluationContext context, Location location, Object base, int key) {
    return EvaluateResult.pure(ConstDataValue.create(this, ((Tuple) base).get(key)));
  }

  @Override
  default EvaluateResult variableTupleFieldAsValue(
      EvaluationContext context, Location location, MortarDeferredCode baseCode, int field) {
    return EvaluateResult.pure(
        new VariableDataValue(
            this, new MortarDeferredCodeAccessTupleField(baseCode, field, jvmDesc())));
  }

  @Override
  default boolean tupleAssignmentCheckFieldAssignableFrom(
      TSList<Error> errors,
      Location location,
      MortarTupleFieldType otherField,
      TSList<Object> path) {
    if (!(otherField instanceof MortarSimpleDataType)) {
      errors.add(new WrongType(location, path, toString(), toString()));
      return false;
    }
    return checkAssignableFrom(errors, location, (MortarDataType) otherField, path);
  }

  @Override
  default EvaluateResult constObjectFieldAsValue(
      EvaluationContext context, Location location, Object base, String name) {
    return EvaluateResult.pure(
        ConstDataValue.create(this, uncheck(() -> base.getClass().getField(name).get(base))));
  }

  @Override
  default MortarTupleFieldType tupleFieldFork() {
    return this;
  }

  @Override
  public default MortarObjectFieldType objectFieldFork() {
    return this;
  }
}
