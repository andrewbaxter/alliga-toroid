package com.zarbosoft.alligatoroid.compiler.jvmshared;

import com.zarbosoft.rendaw.common.ROList;

public class JavaMethodDescriptor {
  public final String value;

  private JavaMethodDescriptor(String value) {
    this.value = value;
  }

  public static JavaMethodDescriptor fromConstructorParts(
      ROList<JavaDataDescriptor> argDescriptors) {
    return fromParts(JavaDataDescriptor.VOID, argDescriptors);
  }

  public static JavaMethodDescriptor fromParts(
          JavaDataDescriptor returnDescriptor, ROList<JavaDataDescriptor> argDescriptors) {
    StringBuilder builder = new StringBuilder();
    builder.append('(');
    for (JavaDataDescriptor d : argDescriptors) {
      builder.append(d.value);
    }
    builder.append(')');
    builder.append(returnDescriptor);
    return new JavaMethodDescriptor(builder.toString());
  }
}
