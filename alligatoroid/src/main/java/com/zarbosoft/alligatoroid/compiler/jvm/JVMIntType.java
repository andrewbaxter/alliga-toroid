package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;

public class JVMIntType implements JVMDataType, SimpleValue {
  public static final JVMIntType value = new JVMIntType();

  private JVMIntType() {}

  @Override
  public int storeOpcode(Module module) {
    return ISTORE;
  }

  @Override
  public int loadOpcode(Module module) {
    return ILOAD;
  }

  @Override
  public String jvmDesc(Module module) {
    return JVMDescriptor.INT_DESCRIPTOR;
  }
}
