package com.zarbosoft.alligatoroid.compiler.jvm.value.halftype;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;

import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;

public class JVMFloatType implements JVMDataType, SimpleValue {
  public static final JVMFloatType value = new JVMFloatType();

  private JVMFloatType() {}

  @Override
  public int storeOpcode() {
    return FSTORE;
  }

  @Override
  public int loadOpcode() {
    return FLOAD;
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.FLOAT_DESCRIPTOR;
  }
}