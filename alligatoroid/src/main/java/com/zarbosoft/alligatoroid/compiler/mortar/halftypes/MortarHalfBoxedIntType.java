package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeInt;

public class MortarHalfBoxedIntType extends MortarHalfObjectType {
  public static final MortarHalfBoxedIntType type = new MortarHalfBoxedIntType();

  private MortarHalfBoxedIntType() {}

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.BOXED_INT;
  }

  @Override
  public Value unlower(Object object) {
    return new WholeInt((Integer) object);
  }
}
