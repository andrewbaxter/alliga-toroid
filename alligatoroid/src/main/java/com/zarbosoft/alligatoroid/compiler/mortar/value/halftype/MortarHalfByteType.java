package com.zarbosoft.alligatoroid.compiler.mortar.value.halftype;

import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
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
  public String jvmDesc() {
    return JVMDescriptor.BYTE_DESCRIPTOR;
  }

  @Override
  public MortarTargetModuleContext.LowerResult box(JVMSharedCode<JVMSharedCode> valueCode) {
    return new MortarTargetModuleContext.LowerResult(
        MortarHalfBoxedByteType.type, new MortarCode().add(valueCode).add(JVMDescriptor.boxByte));
  }

  @Override
  public Value unlower(Object object) {
    throw new Assertion(); // TODO
  }
}
