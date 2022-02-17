package com.zarbosoft.alligatoroid.compiler.jvmshared;

public class JVMSharedFuncDescriptor {
  public final String value;

  private JVMSharedFuncDescriptor(String value) {
    this.value = value;
  }

  public static JVMSharedFuncDescriptor fromConstructorParts(
      JVMSharedDataDescriptor... argDescriptors) {
    return fromParts(JVMSharedDataDescriptor.VOID, argDescriptors);
  }

  public static JVMSharedFuncDescriptor fromParts(
      JVMSharedDataDescriptor returnDescriptor, JVMSharedDataDescriptor... argDescriptors) {
    StringBuilder builder = new StringBuilder();
    builder.append('(');
    for (JVMSharedDataDescriptor d : argDescriptors) {
      builder.append(d.value);
    }
    builder.append(')');
    builder.append(returnDescriptor);
    return new JVMSharedFuncDescriptor(builder.toString());
  }
}
