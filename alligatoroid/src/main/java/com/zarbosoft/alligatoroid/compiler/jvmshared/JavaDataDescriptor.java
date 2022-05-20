package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.rendaw.common.Assertion;

import java.util.Objects;

/** Like La/b/c; */
public class JavaDataDescriptor {
  public static final JavaDataDescriptor BOOL = new JavaDataDescriptor("Z");
  public static final JavaDataDescriptor LONG = new JavaDataDescriptor("J");
  public static final JavaDataDescriptor INT = new JavaDataDescriptor("I");
  public static final JavaDataDescriptor FLOAT = new JavaDataDescriptor("F");
  public static final JavaDataDescriptor DOUBLE = new JavaDataDescriptor("D");
  public static final JavaDataDescriptor BYTE = new JavaDataDescriptor("B");
  public static final JavaDataDescriptor BYTE_ARRAY = BYTE.array();
  public static final JavaDataDescriptor CHAR = new JavaDataDescriptor("C");
  public static final JavaDataDescriptor SHORT = new JavaDataDescriptor("S");
  public static final JavaDataDescriptor VOID = new JavaDataDescriptor("V");
  public static final JavaDataDescriptor OBJECT = JavaDataDescriptor.fromJVMName(JavaInternalName.OBJECT);
  public static final JavaDataDescriptor STRING = JavaDataDescriptor.fromJVMName(JavaInternalName.STRING);
  public static final JavaDataDescriptor BOXED_BOOL = JavaDataDescriptor.fromObjectClass(Boolean.class);
  public static final JavaDataDescriptor BOXED_INT = JavaDataDescriptor.fromObjectClass(Integer.class);
  public static final JavaDataDescriptor BOXED_BYTE = JavaDataDescriptor.fromObjectClass(Byte.class);

  public final String value;

  private JavaDataDescriptor(String value) {
    this.value = value;
  }

  public static JavaDataDescriptor fromJVMName(JavaInternalName name) {
    return new JavaDataDescriptor("L" + name + ";");
  }

  public static JavaDataDescriptor fromObjectClass(Class klass) {
    return fromJVMName(JavaBytecodeUtils.internalNameFromClass(klass));
  }

  public static JavaDataDescriptor fromClass(Class t) {
    if (t == int.class) return INT;
    if (t == byte.class) return BYTE;
    if (t == char.class) return CHAR;
    if (t == short.class) return SHORT;
    if (t == long.class) return LONG;
    if (t == boolean.class) return BOOL;
    if (t == float.class) return FLOAT;
    if (t == double.class) return DOUBLE;
    if (t.isPrimitive()) throw new Assertion();
    return fromObjectClass(t);
  }

  public String toString() {
    return value;
  }

  public JavaDataDescriptor array() {
    return new JavaDataDescriptor("[" + value);
  }

  @Override
  public boolean equals(Object o) {
    return Utils.reflectEquals(this, o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
