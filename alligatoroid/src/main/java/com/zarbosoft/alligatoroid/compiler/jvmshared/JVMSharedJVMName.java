package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;

/** Like a/b/c */
public class JVMSharedJVMName implements AutoBuiltinExportable {
  public static final JVMSharedJVMName BOOL = fromClass(Boolean.class);
  public static final JVMSharedJVMName BYTE = fromClass(Byte.class);
  public static final JVMSharedJVMName INT = fromClass(Integer.class);
  public static final JVMSharedJVMName OBJECT = fromClass(Object.class);
  public static final JVMSharedJVMName STRING = fromClass(String.class);
  @Param public String value;

  private static JVMSharedJVMName fromRaw(String name) {
    final JVMSharedJVMName out = new JVMSharedJVMName();
    out.value = name;
    out.postInit();
    return out;
  }

  public static JVMSharedJVMName fromNormalName(JVMSharedNormalName name) {
    return JVMSharedJVMName.fromRaw(name.value.replace('.', '/'));
  }

  public static JVMSharedJVMName fromClass(Class klass) {
    return JVMSharedJVMName.fromRaw(fromClassInternal(klass));
  }

  private static String fromClassInternal(Class klass) {
    Class enclosing = klass.getNestHost();
    if (enclosing == klass) return klass.getCanonicalName().replace('.', '/');
    else {
      return fromClassInternal(enclosing) + "$" + klass.getSimpleName();
    }
  }

  public String toString() {
    return value;
  }

  @Override
  public void postInit() {}
}
