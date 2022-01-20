package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;

public interface JVMHalfType {
  public JVMValue asValue(JVMProtocode code);
}
