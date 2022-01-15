package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.WholeBool;

public class MortarHalfBoxedBoolType extends MortarHalfObjectType {
  public static final MortarHalfBoxedBoolType type = new MortarHalfBoxedBoolType();

  private MortarHalfBoxedBoolType() {}

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.BOXED_BOOL;
  }

  @Override
  public Value unlower(Object object) {
    return new WholeBool((Boolean) object);
  }
}