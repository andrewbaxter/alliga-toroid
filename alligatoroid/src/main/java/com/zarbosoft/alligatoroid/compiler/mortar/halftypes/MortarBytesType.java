package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WrongType;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.rendaw.common.TSList;

public class MortarBytesType extends MortarBaseObjectType implements MortarPrimitiveType {
  public static final MortarBytesType type = new MortarBytesType();

  @Override
  public JavaDataDescriptor type_jvmDesc() {
    return jvmDesc();
  }

  private static JavaDataDescriptor jvmDesc() {
    return JavaDataDescriptor.BYTE_ARRAY;
  }

  @Override
  public JavaDataDescriptor tuple_fieldtype_jvmDesc() {
    return jvmDesc();
  }
}
