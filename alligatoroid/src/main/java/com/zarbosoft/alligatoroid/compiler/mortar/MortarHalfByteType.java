package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.rendaw.common.Assertion;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;

public class MortarHalfByteType implements MortarHalfDataType {
  public static final MortarHalfByteType type = new MortarHalfByteType();

  private MortarHalfByteType() {}

  @Override
  public int storeOpcode() {
    return ISTORE;
  }

  @Override
  public int loadOpcode() {
    return ILOAD;
  }

  @Override
  public int returnOpcode() {
    return IRETURN;
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.byteDescriptor();
  }

  @Override
  public Value unlower(Object object) {
    throw new Assertion(); // TODO
  }
}