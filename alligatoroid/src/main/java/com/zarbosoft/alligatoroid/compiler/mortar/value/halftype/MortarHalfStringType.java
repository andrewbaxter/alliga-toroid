package com.zarbosoft.alligatoroid.compiler.mortar.value.halftype;

import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfObjectType;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.WholeString;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;

public class MortarHalfStringType extends MortarHalfObjectType {
  public static final MortarHalfStringType type = new MortarHalfStringType();

  private MortarHalfStringType() {}

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
    return JVMDescriptor.stringDescriptor;
  }

  @Override
  public Value unlower(Object object) {
    return new WholeString((String) object);
  }
}