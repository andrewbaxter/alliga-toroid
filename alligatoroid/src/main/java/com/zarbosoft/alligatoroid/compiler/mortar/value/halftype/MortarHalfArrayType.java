package com.zarbosoft.alligatoroid.compiler.mortar.value.halftype;

import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfObjectType;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarHalfArrayType extends MortarHalfObjectType {
  private final MortarHalfDataType elementType;

  public MortarHalfArrayType(MortarHalfDataType elementType) {
    this.elementType = elementType;
  }

  @Override
  public Value unlower(Object object) {
    throw new Assertion(); // TODO
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.arrayDescriptor(elementType.jvmDesc());
  }
}
