package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.SimpleValue;

import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;

public class JVMLongType implements JVMDataType, SimpleValue {
  public static final JVMLongType value = new JVMLongType();

  private JVMLongType() {}

  @Override
  public int storeOpcode(Module module) {
    return LSTORE;
  }

  @Override
  public int loadOpcode(Module module) {
    return LLOAD;
  }

  @Override
  public String jvmDesc(Module module) {
    return JVMDescriptor.LONG_DESCRIPTOR;
  }
}
