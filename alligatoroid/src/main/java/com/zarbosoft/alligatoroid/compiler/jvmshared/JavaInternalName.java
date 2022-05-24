package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinArtifact;

/** Like a/b/c */
public class JavaInternalName implements AutoBuiltinArtifact {
  public static final JavaInternalName BOOL = JavaBytecodeUtils.internalNameFromClass(Boolean.class);
  public static final JavaInternalName BYTE = JavaBytecodeUtils.internalNameFromClass(Byte.class);
  public static final JavaInternalName INT = JavaBytecodeUtils.internalNameFromClass(Integer.class);
  public static final JavaInternalName OBJECT = JavaBytecodeUtils.internalNameFromClass(Object.class);
  public static final JavaInternalName STRING = JavaBytecodeUtils.internalNameFromClass(String.class);
  @Param public String value;

  public String toString() {
    return value;
  }

  @Override
  public void postInit() {}
}
