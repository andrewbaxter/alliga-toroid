package com.zarbosoft.alligatoroid.compiler.builtin;

import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;

@StaticAutogen.BuiltinAggregate
public class BuiltinJavaBytecodeDescriptors {
  public static final MortarDataType type =
      StaticAutogen.autoMortarHalfObjectTypes.get(JavaDataDescriptor.class);
  public static final JavaDataDescriptor _int = JavaDataDescriptor.INT;
  public static final JavaDataDescriptor _bool = JavaDataDescriptor.BOOL;
  public static final JavaDataDescriptor string = JavaDataDescriptor.STRING;
}
