package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;

public class MortarCode extends JVMSharedCode<MortarCode> {
  public static final String MORTAR_TARGET_NAME = "mortar";

  @Override
  public String targetName() {
    return MORTAR_TARGET_NAME;
  }
}
