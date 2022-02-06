package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.jvm.value.JVMValue;

public interface JVMHalfType {
  public JVMValue asValue(JVMProtocode code);
}
