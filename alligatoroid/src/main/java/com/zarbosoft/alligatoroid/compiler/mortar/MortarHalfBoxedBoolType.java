package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.model.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.rendaw.common.Assertion;

public class MortarHalfBoxedBoolType extends MortarHalfObjectType {
  public static final MortarHalfBoxedBoolType type = new MortarHalfBoxedBoolType();

  private MortarHalfBoxedBoolType() {}

  @Override
  public String jvmDesc() {
    return JVMDescriptor.objDescriptorFromReal(Boolean.class);
  }

  @Override
  public Value unlower(Object object) {
    throw new Assertion(); // TODO
  }
}
