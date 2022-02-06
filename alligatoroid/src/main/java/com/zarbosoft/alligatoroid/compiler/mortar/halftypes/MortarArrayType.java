package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluateResult;
import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.NoField;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataArrayElementValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ConstDataStackValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.DataValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataArrayElementValue;

import java.lang.reflect.Array;

public class MortarArrayType extends MortarObjectType {
  public final MortarDataType elementType;

  public MortarArrayType(MortarDataType elementType) {
    this.elementType = elementType;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return elementType.jvmDesc().array();
  }

  @Override
  public EvaluateResult constValueAccess(
      EvaluationContext context, Location location, Object array, Value field) {
    if (!(field.getClass() == ConstDataStackValue.class)) {
      return variableValueAccess(
          context,
          location,
          MortarCarry.ofDeferredHalf(c -> constValueVary(context, array)),
          field);
    }
    if (!MortarIntType.type.assertAssignableFrom(context, location, field))
      return EvaluateResult.error;
    int index = (Integer) ((ConstDataStackValue) field).value;
    if (index < 0 || index >= Array.getLength(array)) {
      context.moduleContext.errors.add(new NoField(location, index));
      return null;
    }
    return EvaluateResult.pure(new ConstDataArrayElementValue(elementType, array, index));
  }

  @Override
  public Error checkAssignableFrom(Location location, MortarDataType type) {
    if (!(type instanceof MortarArrayType))
      return new WrongType(location, type.toString(), this.toString());
    final Error error =
        elementType.checkAssignableFrom(location, ((MortarArrayType) type).elementType);
    if (error != null) return error;
    return null;
  }

  @Override
  public EvaluateResult variableValueAccess(
      EvaluationContext context, Location location, MortarCarry arrayCarry, Value field) {
    if (!MortarIntType.type.assertAssignableFrom(context, location, field))
      return EvaluateResult.error;
    MortarCarry fieldCarry = ((DataValue) field).mortarVaryCode(context, location);
    return EvaluateResult.pure(
        new VariableDataArrayElementValue(
            elementType,
            MortarCarry.ofHalf(
                new JVMSharedCode().add(arrayCarry.half(context)).add(fieldCarry.half(context)),
                new JVMSharedCode()
                    .add(fieldCarry.drop(context, location))
                    .add(arrayCarry.drop(context, location)))));
  }
}