package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarHalfBoxedByteType extends MortarHalfObjectType {
  public static final MortarHalfBoxedByteType type = new MortarHalfBoxedByteType();

  private MortarHalfBoxedByteType() {}

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.BOXED_BYTE;
  }

  @Override
  public Value unlower(Object object) {
    throw new Assertion(); // TODO
  }
}
