package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMClassInstanceType;

public class JVMConstructor {
  public final JVMUtils.MethodSpecDetails specDetails;
  public JVMClassInstanceType type;

  public JVMConstructor(JVMClassInstanceType type, JVMUtils.MethodSpecDetails specDetails) {
    this.type = type;
    this.specDetails = specDetails;
  }
}
