package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.TSMap;

public class JVMArrayType extends JVMObjectType implements GraphSerializable {
  public final JVMDataType elementType;

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

  @Override
  public Record graphSerialize() {
    return new Record(new TSMap<>(s -> s.putNew("element", elementType)));
  }
}
