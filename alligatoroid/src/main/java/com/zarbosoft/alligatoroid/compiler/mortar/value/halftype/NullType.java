package com.zarbosoft.alligatoroid.compiler.mortar.value.halftype;

import com.zarbosoft.alligatoroid.compiler.mortar.value.base.AutoGraphMixin;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedCode;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.LeafValue;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.MortarHalfDataType;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.mortar.value.whole.NullValue;
import com.zarbosoft.rendaw.common.Assertion;

import static org.objectweb.asm.Opcodes.RETURN;

public class NullType implements MortarHalfDataType, AutoGraphMixin, LeafValue {
  public static final NullType type = new NullType();

  private NullType() {}

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
  public String jvmDesc() {
    return JVMDescriptor.VOID_DESCRIPTOR;
  }

  @Override
  public MortarTargetModuleContext.LowerResult box(JVMSharedCode<JVMSharedCode> valueCode) {
    return new MortarTargetModuleContext.LowerResult(this, valueCode);
  }

  @Override
  public Value unlower(Object object) {
    return NullValue.value;
  }
}
