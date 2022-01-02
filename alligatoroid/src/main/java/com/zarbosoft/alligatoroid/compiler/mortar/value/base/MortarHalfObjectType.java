package com.zarbosoft.alligatoroid.compiler.mortar.value.base;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;

public abstract class MortarHalfObjectType implements MortarHalfDataType {
  protected MortarHalfObjectType() {}

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
  public MortarTargetModuleContext.LowerResult box(JVMSharedCode<JVMSharedCode> valueCode) {
    return new MortarTargetModuleContext.LowerResult(this, valueCode);
  }
}
