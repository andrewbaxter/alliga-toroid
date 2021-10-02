package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.rendaw.common.Assertion;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;

public class MortarHalfBoolType implements MortarHalfDataType {
  public static final MortarHalfBoolType type = new MortarHalfBoolType();

  private MortarHalfBoolType() {}

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
    return JVMDescriptor.BOOL_DESCRIPTOR;
  }

  @Override
  public MortarTargetModuleContext.LowerResult box(JVMSharedCode valueCode) {
    return new MortarTargetModuleContext.LowerResult(
        MortarHalfBoxedBoolType.type,
        new MortarCode()
            .add(valueCode)
            .add(JVMDescriptor.boxBool));
  }

  @Override
  public Value unlower(Object object) {
    throw new Assertion(); // TODO
  }
}
