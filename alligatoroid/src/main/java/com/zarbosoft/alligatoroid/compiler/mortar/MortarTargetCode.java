package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.TargetCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;

public class MortarTargetCode implements TargetCode {
  public static final MortarTargetCode empty = new MortarTargetCode(null);
  public final JavaBytecode e;

  public static JavaBytecode ex(TargetCode other) {
    if (other == null) {
      return null;
    }
    return ((MortarTargetCode) other).e;
  }

  public MortarTargetCode(JavaBytecode e) {
    this.e = e;
  }

  @Override
  public String targetName() {
    return "mortar";
  }
}
