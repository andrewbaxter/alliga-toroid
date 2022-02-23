package com.zarbosoft.alligatoroid.compiler.jvm.halftypes;

import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;

public class JVMArrayType implements AutoBuiltinExportable, JVMBaseObjectType {
  @Param public JVMType elementType;

  public static JVMArrayType graphDeserialize(Record data) {
    return create((JVMType) data.data.get("element"));
  }

  public static JVMArrayType create(JVMType elementType) {
    final JVMArrayType out = new JVMArrayType();
    out.elementType = elementType;
    return out;
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return elementType.jvmDesc().array();
  }
}
