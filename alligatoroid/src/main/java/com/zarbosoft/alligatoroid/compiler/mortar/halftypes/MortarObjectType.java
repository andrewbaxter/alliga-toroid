package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.EvaluationContext;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCodeElement;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;

public abstract class MortarObjectType implements MortarDataType {
  protected MortarObjectType() {}

  @Override
  public int storeOpcode() {
    return ASTORE;
  }

  @Override
  public int loadOpcode() {
    return ALOAD;
  }

  @Override
  public int returnOpcode() {
    return ARETURN;
  }

  @Override
  public MortarTargetModuleContext.HalfLowerResult box(JVMSharedCodeElement valueCode) {
    return new MortarTargetModuleContext.HalfLowerResult(this, valueCode);
  }

  @Override
  public JVMSharedCodeElement constValueVary(EvaluationContext context, Object value) {
    return ((MortarTargetModuleContext) context.target).transfer(value);
  }
}
