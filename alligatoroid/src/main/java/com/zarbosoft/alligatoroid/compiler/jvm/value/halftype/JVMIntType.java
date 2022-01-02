package com.zarbosoft.alligatoroid.compiler.jvm.value.halftype;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;

public class JVMIntType implements JVMDataType, SimpleValue {
  public static final JVMIntType value = new JVMIntType();

  private JVMIntType() {}

  @Override
  public int storeOpcode() {
    return ISTORE;
  }

  @Override
  public int loadOpcode() {
    return ILOAD;
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.INT_DESCRIPTOR;
  }
}
