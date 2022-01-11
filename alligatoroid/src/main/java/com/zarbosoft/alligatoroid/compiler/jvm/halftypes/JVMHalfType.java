package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;

public interface JVMHalfType {
  public Value asValue(JVMProtocode code);
}
