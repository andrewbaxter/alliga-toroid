package com.zarbosoft.alligatoroid.compiler.mortar.value.halftype;

import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfObjectType;
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
