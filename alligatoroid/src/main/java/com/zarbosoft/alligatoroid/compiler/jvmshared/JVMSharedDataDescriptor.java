package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.rendaw.common.Assertion;

import java.util.Objects;

/** Like La/b/c; */
public class JVMSharedDataDescriptor {
  public static final JVMSharedDataDescriptor BOOL = new JVMSharedDataDescriptor("Z");
  public static final JVMSharedDataDescriptor LONG = new JVMSharedDataDescriptor("J");
  public static final JVMSharedDataDescriptor INT = new JVMSharedDataDescriptor("I");
  public static final JVMSharedDataDescriptor FLOAT = new JVMSharedDataDescriptor("F");
  public static final JVMSharedDataDescriptor DOUBLE = new JVMSharedDataDescriptor("D");
  public static final JVMSharedDataDescriptor BYTE = new JVMSharedDataDescriptor("B");
  public static final JVMSharedDataDescriptor BYTE_ARRAY = BYTE.array();
  public static final JVMSharedDataDescriptor CHAR = new JVMSharedDataDescriptor("C");
  public static final JVMSharedDataDescriptor SHORT = new JVMSharedDataDescriptor("S");
  public static final JVMSharedDataDescriptor VOID = new JVMSharedDataDescriptor("V");
  public static final JVMSharedDataDescriptor OBJECT =
      JVMSharedDataDescriptor.fromJVMName(JVMSharedJVMName.OBJECT);
  public static final JVMSharedDataDescriptor STRING =
      JVMSharedDataDescriptor.fromJVMName(JVMSharedJVMName.STRING);
  public static final JVMSharedDataDescriptor BOXED_BOOL =
      JVMSharedDataDescriptor.fromObjectClass(Boolean.class);
  public static final JVMSharedDataDescriptor BOXED_INT =
      JVMSharedDataDescriptor.fromObjectClass(Integer.class);
  public static final JVMSharedDataDescriptor BOXED_BYTE =
      JVMSharedDataDescriptor.fromObjectClass(Byte.class);

  public final String value;

  private JVMSharedDataDescriptor(String value) {
    this.value = value;
  }

  public static JVMSharedDataDescriptor fromJVMName(JVMSharedJVMName name) {
    return new JVMSharedDataDescriptor("L" + name + ";");
  }

  public static JVMSharedDataDescriptor fromObjectClass(Class klass) {
    return fromJVMName(JVMSharedJVMName.fromClass(klass));
  }

  public static JVMSharedDataDescriptor fromClass(Class t) {
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

  public JVMSharedDataDescriptor array() {
    return new JVMSharedDataDescriptor("[" + value);
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
