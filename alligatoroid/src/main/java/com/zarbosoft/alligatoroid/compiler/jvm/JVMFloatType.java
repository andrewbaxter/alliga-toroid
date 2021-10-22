package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;

import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;

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
