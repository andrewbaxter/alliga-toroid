package com.zarbosoft.alligatoroid.compiler.jvm.value.halftype;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;

import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;

public class JVMFloatType implements JVMDataType {
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
  public JVMSharedDataDescriptor jvmDesc() {
    return JVMSharedDataDescriptor.FLOAT;
  }
}
