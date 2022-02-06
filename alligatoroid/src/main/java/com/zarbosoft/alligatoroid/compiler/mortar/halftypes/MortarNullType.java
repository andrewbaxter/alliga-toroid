package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeInstruction;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.rendaw.common.Assertion;

import static org.objectweb.asm.Opcodes.RETURN;

public class MortarNullType implements MortarDataType {
  public static final MortarNullType type = new MortarNullType();

  private MortarNullType() {}

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
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.VOID;
  }

  @Override
  public MortarTargetModuleContext.HalfLowerResult box(JVMSharedCodeElement valueCode) {
    return new MortarTargetModuleContext.HalfLowerResult(this, valueCode);
  }

  @Override
  public JVMSharedCodeElement constValueVary(EvaluationContext context, Object value) {
    return JVMSharedCodeInstruction.null_;
  }
}
