package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;

public class JVMHalfHalfArrayType extends JVMHalfObjectType {
  public JVMHalfDataType elementType;

  public JVMHalfHalfArrayType(JVMHalfDataType elementType) {
    this.elementType = elementType;
  }

  public static JVMHalfHalfArrayType graphDeserialize(Record data) {
    return new JVMHalfHalfArrayType((JVMHalfDataType) data.data.get("element"));
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return elementType.jvmDesc().array();
  }
}
