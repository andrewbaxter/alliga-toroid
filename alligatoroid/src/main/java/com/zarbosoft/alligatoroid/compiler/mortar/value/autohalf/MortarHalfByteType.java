package com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
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
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.BYTE;
  }

  @Override
  public MortarTargetModuleContext.LowerResult box(JVMSharedCodeElement valueCode) {
    return new MortarTargetModuleContext.LowerResult(
        MortarHalfBoxedByteType.type, new JVMSharedCode().add(valueCode).add(JVMSharedCode.boxByte));
  }

  @Override
  public Value unlower(Object object) {
    throw new Assertion(); // TODO
  }
}
