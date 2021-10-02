package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;

import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;

public class JVMCharType implements JVMDataType, SimpleValue {
  public static final JVMCharType value = new JVMCharType();

  private JVMCharType() {}

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
    return JVMDescriptor.CHAR_DESCRIPTOR;
  }
}
