package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportable;
import com.zarbosoft.alligatoroid.compiler.inout.graph.BuiltinAutoExportableType;

/** Like a/b/c */
public class JavaInternalName implements BuiltinAutoExportable {
  public static final JavaInternalName BOXED_BOOL = JavaBytecodeUtils.internalNameFromClass(Boolean.class);
  public static final JavaInternalName BOXED_BYTE = JavaBytecodeUtils.internalNameFromClass(Byte.class);
  public static final JavaInternalName BOXED_INT = JavaBytecodeUtils.internalNameFromClass(Integer.class);
  public static final JavaInternalName OBJECT = JavaBytecodeUtils.internalNameFromClass(Object.class);
  public static final JavaInternalName STRING = JavaBytecodeUtils.internalNameFromClass(String.class);
  public static final JavaInternalName RUNTIME_EXCEPTION = JavaBytecodeUtils.internalNameFromClass(RuntimeException.class);
  @BuiltinAutoExportableType.Param
  public String value;

  public String toString() {
    return value;
  }

  @Override
  public void postInit() {}
}
