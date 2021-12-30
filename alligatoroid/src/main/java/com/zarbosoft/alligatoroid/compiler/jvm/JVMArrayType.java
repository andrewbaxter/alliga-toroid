package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;

public class JVMArrayType extends JVMObjectType {
  public JVMDataType elementType;

  public JVMArrayType(JVMDataType elementType) {
    this.elementType = elementType;
  }

  public static JVMArrayType graphDeserialize(Record data) {
    return new JVMArrayType((JVMDataType) data.data.get("element"));
  }

  @Override
  public String jvmDesc() {
    return JVMDescriptor.arrayDescriptor(elementType.jvmDesc());
  }
}
