package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportableType;

/** Like a/b/c */
public class JavaInternalName implements AutoBuiltinExportable {
  public static final JavaInternalName BOOL = JavaBytecodeUtils.internalNameFromClass(Boolean.class);
  public static final JavaInternalName BYTE = JavaBytecodeUtils.internalNameFromClass(Byte.class);
  public static final JavaInternalName INT = JavaBytecodeUtils.internalNameFromClass(Integer.class);
  public static final JavaInternalName OBJECT = JavaBytecodeUtils.internalNameFromClass(Object.class);
  public static final JavaInternalName STRING = JavaBytecodeUtils.internalNameFromClass(String.class);
  @AutoBuiltinExportableType.Param
  public String value;

  public String toString() {
    return value;
  }

  @Override
  public void postInit() {}
}
