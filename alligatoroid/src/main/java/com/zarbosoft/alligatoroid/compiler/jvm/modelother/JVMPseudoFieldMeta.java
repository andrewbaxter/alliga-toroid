package com.zarbosoft.alligatoroid.compiler.jvm.modelother;

import com.zarbosoft.alligatoroid.compiler.jvm.JVMUtils;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMClassInstanceType;
import com.zarbosoft.rendaw.common.TSList;

public class JVMPseudoFieldMeta {
  public final String name;
  public final TSList<JVMUtils.MethodSpecDetails> methods;
  public JVMClassInstanceType base;
  public JVMUtils.DataSpecDetails data;

  public JVMPseudoFieldMeta(
          JVMClassInstanceType base, String name, TSList<JVMUtils.MethodSpecDetails> methods, JVMUtils.DataSpecDetails data) {
    this.base = base;
    this.name = name;
    this.methods = methods;
    this.data = data;
  }

  public static JVMPseudoFieldMeta blank(JVMClassInstanceType base, String name) {
    return new JVMPseudoFieldMeta(base, name, new TSList<>(), null);
  }
}
