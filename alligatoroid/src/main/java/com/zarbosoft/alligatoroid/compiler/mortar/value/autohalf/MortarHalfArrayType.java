package com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
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
  public JVMSharedDataDescriptor jvmDesc() {
  return elementType.jvmDesc().array();
  }
}
