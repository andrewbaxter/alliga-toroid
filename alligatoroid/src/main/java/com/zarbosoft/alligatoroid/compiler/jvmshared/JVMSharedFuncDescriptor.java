package com.zarbosoft.alligatoroid.compiler.jvmshared;

public class JVMSharedFuncDescriptor {
  final String value;

  private JVMSharedFuncDescriptor(String value) {
    this.value = value;
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
