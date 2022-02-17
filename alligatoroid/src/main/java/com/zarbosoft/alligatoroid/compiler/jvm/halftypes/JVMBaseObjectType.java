package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;

public interface JVMBaseObjectType extends JVMType {
  @Override
  default public int storeOpcode() {
    return ASTORE;
  }

  @Override
  default public int loadOpcode() {
    return ALOAD;
  }
}
