package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarHalfBoxedByteType extends MortarHalfObjectType {
  public static final MortarHalfBoxedByteType type = new MortarHalfBoxedByteType();

  private MortarHalfBoxedByteType() {}

  @Override
  public String jvmDesc() {
    return JVMDescriptor.objDescriptorFromReal(Byte.class);
  }

  @Override
  public Value unlower(Object object) {
    throw new Assertion(); // TODO
  }
}
