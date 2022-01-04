package com.zarbosoft.alligatoroid.compiler.jvmshared;

/** Like a.b.c */
public class JVMSharedNormalName {
  final String value;

  private JVMSharedNormalName(String value) {
    this.value = value;
  }

  public static JVMSharedNormalName fromString(String name) {
    return new JVMSharedNormalName(name);
  }

  public String toString() {
    return value;
  }
}
