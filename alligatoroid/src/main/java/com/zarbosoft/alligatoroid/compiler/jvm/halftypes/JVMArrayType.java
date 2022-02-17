package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;

public class JVMArrayType implements AutoBuiltinExportable, JVMBaseObjectType {
  public JVMType elementType;

  public JVMArrayType(JVMType elementType) {
    this.elementType = elementType;
  }

  public static JVMArrayType graphDeserialize(Record data) {
    return new JVMArrayType((JVMType) data.data.get("element"));
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return elementType.jvmDesc().array();
  }
}
