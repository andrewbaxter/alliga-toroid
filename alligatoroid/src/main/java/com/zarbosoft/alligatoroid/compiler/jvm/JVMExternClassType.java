package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class JVMExternClassType extends JVMBaseClassType implements GraphSerializable {
  public static final String SERIAL_NAME = "jvmName";
  public static final String SERIAL_FIELDS = "fields";
  boolean finished;

  public JVMExternClassType(String jvmExternalClass) {
    super(jvmExternalClass, new TSMap<>());
    finished = false;
  }

  private JVMExternClassType(String jvmExternalClass, TSMap<Object, JVMType> fields) {
    super(jvmExternalClass, fields);
    finished = true;
  }

  public static JVMExternClassType graphDeserialize(Record record) {
    JVMExternClassType out =
        new JVMExternClassType(
            (String) record.data.get(SERIAL_NAME),
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS)).data);
    for (Map.Entry<Object, JVMType> e : out.fields) {
      if (e.getValue() instanceof JVMShallowMethodFieldType)
        ((JVMShallowMethodFieldType) e.getValue()).base = out;
    }
    return out;
  }

  public void defineMethod(String name, Record spec) {
    // TODO take internal name as well
    JVMShallowMethodFieldType.MethodSpecDetails specDetails =
        JVMShallowMethodFieldType.specDetails(spec);
    JVMShallowMethodFieldType field =
        new JVMShallowMethodFieldType(specDetails.returnType, name, specDetails.jvmSigDesc);
    field.base = this;
    fields.putNew(name, field);
  }

  @Override
  public Record graphSerialize() {
    return new Record(
        new TSMap<Object, Object>()
            .put(SERIAL_NAME, jvmInternalClass)
            .put(SERIAL_FIELDS, new Record((TSMap) fields)));
  }
}
