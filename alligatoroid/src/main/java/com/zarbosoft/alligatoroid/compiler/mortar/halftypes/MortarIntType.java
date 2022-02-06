package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarCarry;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;

public class MortarIntType implements MortarDataType {
  public static final MortarIntType type = new MortarIntType();

  private MortarIntType() {}

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
    return JVMSharedDataDescriptor.INT;
  }

  @Override
  public MortarTargetModuleContext.HalfLowerResult box(JVMSharedCodeElement valueCode) {
    return new MortarTargetModuleContext.HalfLowerResult(
        MortarBoxedIntType.type, new JVMSharedCode().add(valueCode).add(JVMSharedCode.boxInt));
  }

  @Override
  public MortarCarry valueVary(EvaluationContext context, MortarCarry carry) {
    if (!carry.isWhole()) return carry;
    return MortarCarry.ofDeferredHalf(c -> new JVMSharedCode().addInt((Integer) carry.whole()));
  }
}
