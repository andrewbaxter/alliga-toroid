package com.zarbosoft.alligatoroid.compiler.jvm.value.halftype;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.base.SimpleValue;

import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;

public class JVMLongType implements JVMDataType, SimpleValue {
  public static final JVMLongType value = new JVMLongType();

  private JVMLongType() {}

  @Override
  public int storeOpcode() {
    return LSTORE;
  }

  @Override
  public int loadOpcode() {
    return LLOAD;
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.LONG_DESCRIPTOR;
  }
}
