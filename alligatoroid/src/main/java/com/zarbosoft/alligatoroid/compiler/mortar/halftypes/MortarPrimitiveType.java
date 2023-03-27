package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarSimpleDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.deferredcode.MortarDeferredCode;

public interface MortarPrimitiveType extends MortarSimpleDataType {
  @Override
  default boolean type_canCastTo(
      MortarDataPrototype prototype) {
    return (prototype instanceof MortarPrimitivePrototype)
        && ((MortarPrimitivePrototype) prototype).type == this;
  }

  @Override
  default JavaBytecode type_castTo(MortarDataPrototype prototype, MortarDeferredCode code) {
    return code.consume();
  }
}
