package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.rendaw.common.TSList;

public class MortarBytesType extends MortarBaseObjectType implements MortarSimpleDataType {
  public static final MortarBytesType type = new MortarBytesType();

  @Override
  public JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.BYTE_ARRAY;
  }

  @Override
  public boolean checkAssignableFrom(
      TSList<Error> errors, Location location, MortarDataType type, TSList<Object> path) {
    if (type instanceof MortarImmutableType) type = ((MortarImmutableType) type).innerType;
    if (type != this) {
      errors.add(new WrongType(location, path, type.toString(), toString()));
      return false;
    }
    return true;
  }
}
