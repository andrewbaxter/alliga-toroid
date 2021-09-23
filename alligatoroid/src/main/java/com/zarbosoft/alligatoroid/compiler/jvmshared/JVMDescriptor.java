package com.zarbosoft.alligatoroid.compiler.jvmshared;

/**
 * 3 types of strings:
 *
 * <p>1. Normal names (a.b.c.MyClass - objects only)
 *
 * <p>2. JVM names (a/b/c/MyClass - objects only)
 *
 * <p>3. Descriptors (La/b/c/MyClass; - all types)
 */
public class JVMDescriptor {
  public static final String objectJvmName = jvmName(Object.class);
  public static final String objectDescriptor = objDescriptorFromJvmName(objectJvmName);
  public static final String stringJvmName = jvmName(String.class);
  public static final String stringDescriptor = objDescriptorFromJvmName(stringJvmName);
  public static final String boolDescriptor = "Z";

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

  public static String voidDescriptor() {
    return "V";
  }

  public static String shortDescriptor() {
    return "S";
  }

  public static String charDescriptor() {
    return "C";
  }

  public static String byteDescriptor() {
    return "B";
  }

  public static String doubleDescriptor() {
    return "D";
  }

  public static String floatDescriptor() {
    return "F";
  }

  public static String intDescriptor() {
    return "I";
  }

  public static String longDescriptor() {
    return "J";
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
