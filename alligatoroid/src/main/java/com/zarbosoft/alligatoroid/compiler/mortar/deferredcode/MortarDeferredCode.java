package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;

public interface MortarDeferredCode {
  JavaBytecode drop();

  JavaBytecode consume();

  JavaBytecode set(JavaBytecode value);
}
