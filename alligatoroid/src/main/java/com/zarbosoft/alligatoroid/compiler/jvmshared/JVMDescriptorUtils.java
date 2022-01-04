package com.zarbosoft.alligatoroid.compiler.jvmshared;

import org.objectweb.asm.tree.MethodInsnNode;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * 3 types of strings:
 *
 * <p>1. Normal names (a.b.c.MyClass - objects only)
 *
 * <p>2. JVM names (a/b/c/MyClass - objects only)
 *
 * <p>3. Descriptors (La/b/c/MyClass; - all types)
 */
public class JVMDescriptorUtils {
  public static final String objectJvmName = jvmName(Object.class);
  public static final String objectDescriptor = objDescriptorFromJvmName(objectJvmName);
  public static final String stringJvmName = jvmName(String.class);
  public static final String stringDescriptor = objDescriptorFromJvmName(stringJvmName);
  public static final String BOOL_DESCRIPTOR = "Z";
  public static final String LONG_DESCRIPTOR = "J";
  public static final String INT_DESCRIPTOR = "I";
  public static final String FLOAT_DESCRIPTOR = "F";
  public static final String DOUBLE_DESCRIPTOR = "D";
  public static final String BYTE_DESCRIPTOR = "B";
  public static final String CHAR_DESCRIPTOR = "C";
  public static final String SHORT_DESCRIPTOR = "S";
  public static final String VOID_DESCRIPTOR = "V";

  public static String func(String returnDescriptor, String... argDescriptors) {
    StringBuilder builder = new StringBuilder();
    builder.append('(');
    for (String d : argDescriptors) {
      builder.append(d);
    }
    builder.append(')');
    builder.append(returnDescriptor);
    return builder.toString();
  }

  public static String objDescriptorFromReal(Class klass) {
    return objDescriptorFromJvmName(jvmName(klass));
  }

  public static String objDescriptorFromJvmName(String jvmName) {
    return "L" + jvmName + ";";
  }

  /**
   * Converts a.b.c to a/b/c
   *
   * @param externalName
   * @return
   */
  public static String jvmName(String externalName) {
    return externalName.replace('.', '/');
  }

  public static String jvmName(Class klass) {
    Class enclosing = klass.getNestHost();
    if (enclosing == klass) return klass.getCanonicalName().replace('.', '/');
    else {
      return jvmName(enclosing) + "$" + klass.getSimpleName();
    }
  }

  public static String arrayDescriptor(String child) {
    return "[" + child;
  }
}
