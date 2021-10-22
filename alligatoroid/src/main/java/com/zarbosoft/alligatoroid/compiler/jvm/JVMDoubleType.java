package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;

import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DSTORE;

public class JVMDoubleType implements JVMDataType, SimpleValue {
  public static final JVMDoubleType value = new JVMDoubleType();

  private JVMDoubleType() {}

  @Override
  public int storeOpcode() {
    return DSTORE;
  }

  @Override
  public int loadOpcode() {
    return DLOAD;
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.DOUBLE_DESCRIPTOR;
  }
}
