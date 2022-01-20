package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
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
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.BYTE;
  }

  @Override
  public MortarTargetModuleContext.HalfLowerResult box(JVMSharedCodeElement valueCode) {
    return new MortarTargetModuleContext.HalfLowerResult(
        MortarHalfBoxedByteType.type, new JVMSharedCode().add(valueCode).add(JVMSharedCode.boxByte));
  }

  @Override
  public MortarValue unlower(Object object) {
    throw new Assertion(); // TODO
  }
}
