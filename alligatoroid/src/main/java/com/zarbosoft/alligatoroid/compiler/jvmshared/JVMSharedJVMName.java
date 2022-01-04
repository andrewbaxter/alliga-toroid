package com.zarbosoft.alligatoroid.compiler.jvmshared;

/** Like a/b/c */
public class JVMSharedJVMName {
  public static final JVMSharedJVMName BOOL = fromClass(Boolean.class);
  public static final JVMSharedJVMName BYTE = fromClass(Byte.class);
  public static final JVMSharedJVMName INT = fromClass(Integer.class);
  public static final JVMSharedJVMName OBJECT = fromClass(Object.class);
  public static final JVMSharedJVMName STRING = fromClass(String.class);
  final String value;

  private JVMSharedJVMName(String value) {
    this.value = value;
  }

  public static JVMSharedJVMName fromNormalName(JVMSharedNormalName name) {
    return new JVMSharedJVMName(name.value.replace('.', '/'));
  }

  public static JVMSharedJVMName fromClass(Class klass) {
    return new JVMSharedJVMName(fromClassInternal(klass));
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
}
