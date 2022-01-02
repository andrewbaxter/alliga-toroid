package com.zarbosoft.alligatoroid.compiler.jvm.value.base;

import com.zarbosoft.alligatoroid.compiler.jvm.JVMProtocode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;

public interface JVMType {
  public Value asValue(JVMProtocode code);
}
