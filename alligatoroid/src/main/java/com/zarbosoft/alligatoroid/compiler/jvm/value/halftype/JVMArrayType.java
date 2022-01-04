package com.zarbosoft.alligatoroid.compiler.jvm.value.halftype;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptorUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Record;

public class JVMArrayType extends JVMObjectType {
  public JVMDataType elementType;

  public JVMArrayType(JVMDataType elementType) {
    this.elementType = elementType;
  }

  public static JVMArrayType graphDeserialize(Record data) {
    return new JVMArrayType((JVMDataType) data.data.get("element"));
  }

  @Override
  public JVMSharedDataDescriptor jvmDesc() {
    return elementType.jvmDesc().array();
  }
}
