package com.zarbosoft.alligatoroid.compiler.builtin;

import com.zarbosoft.alligatoroid.compiler.Meta;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarDataType;

@Meta.BuiltinAggregate
public class BuiltinJavaBytecodeDescriptors {
  public static final MortarDataType type =
      Meta.autoMortarHalfDataTypes.get(JavaDataDescriptor.class);
  public static final JavaDataDescriptor _int = JavaDataDescriptor.INT;
  public static final JavaDataDescriptor _bool = JavaDataDescriptor.BOOL;
  public static final JavaDataDescriptor string = JavaDataDescriptor.STRING;
}
