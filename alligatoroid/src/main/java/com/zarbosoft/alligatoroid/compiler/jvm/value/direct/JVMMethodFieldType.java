package com.zarbosoft.alligatoroid.compiler.jvm.value.direct;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvm.value.whole.JVMClassType;

/** Represents the metadata for interacting with (calling) a method. */
public class JVMMethodFieldType {
  public final JVMDataType returnType;
  public final String name;
  public final String jvmDesc;
  public JVMClassType base;

  public JVMMethodFieldType(JVMDataType returnType, String name, String jvmDesc) {
    this.returnType = returnType;
    this.name = name;
    this.jvmDesc = jvmDesc;
  }
}
