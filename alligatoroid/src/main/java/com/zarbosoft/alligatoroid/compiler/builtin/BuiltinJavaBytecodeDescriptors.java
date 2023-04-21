package com.zarbosoft.alligatoroid.compiler.builtin;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;
import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;

@StaticAutogen.BuiltinAggregate
public class BuiltinJavaBytecodeDescriptors {
  public final AlligatorusType type =
      StaticAutogen.autoMortarObjectTypes.get(JavaDataDescriptor.class);
  public final JavaDataDescriptor _int = Global.DESC_INT;
  public final JavaDataDescriptor _bool = Global.DESC_BOOL;
  public final JavaDataDescriptor string = Global.DESC_STRING;
}
