package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.rendaw.common.Assertion;

import static org.objectweb.asm.Opcodes.RETURN;

public class NullType implements MortarHalfDataType {
  public static final NullType type = new NullType();

  private NullType() {}

  @Override
  public int storeOpcode() {
    throw new Assertion();
  }

  @Override
  public int loadOpcode() {
    throw new Assertion();
  }

  @Override
  public int returnOpcode() {
    return RETURN;
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.voidDescriptor();
  }

  @Override
  public Value unlower(Object object) {
    return NullValue.value;
  }
}
