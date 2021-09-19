package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class JVMExternClassType extends JVMBaseClassType implements GraphSerializable {
  public static final String SERIAL_NAME = "jvmName";
  public static final String SERIAL_FIELDS_METHODS = "methods";
  public static final String SERIAL_FIELDS_DATA = "data";
  public static final String SERIAL_FIELDS_STATIC_METHODS = "staticMethods";
  public static final String SERIAL_FIELDS_STATIC_DATA = "staticData";
  public static final String SERIAL_INHERITS = "inherits";
  boolean finished;

  public JVMExternClassType(String jvmExternalClass) {
    super(
        jvmExternalClass,
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSList<>());
    finished = false;
  }

  private JVMExternClassType(
      String jvmExternalClass,
      TSMap<String, JVMDataType> dataFields,
      TSMap<ROTuple, JVMShallowMethodFieldType> methodFields,
      TSMap<String, JVMDataType> staticDataFields,
      TSMap<ROTuple, JVMShallowMethodFieldType> staticMethodFields,
      TSList<JVMBaseClassType> inherits) {
    super(
        jvmExternalClass, dataFields, methodFields, staticDataFields, staticMethodFields, inherits);
    finished = true;
  }

  public static JVMExternClassType graphDeserialize(Record record) {
    JVMExternClassType out =
        new JVMExternClassType(
            (String) record.data.get(SERIAL_NAME),
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS_DATA)).data,
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS_METHODS)).data,
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS_DATA)).data,
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS_METHODS)).data,
            (TSList) ((Tuple) record.data.get(SERIAL_INHERITS)).data);
    for (Map.Entry<ROTuple, JVMShallowMethodFieldType> e : out.methodFields) {
      if (e.getValue() instanceof JVMShallowMethodFieldType)
        ((JVMShallowMethodFieldType) e.getValue()).base = out;
    }
    return out;
  }

  @Override
  public Record graphSerialize() {
    return new Record(
        new TSMap<Object, Object>()
            .put(SERIAL_NAME, jvmInternalClass)
            .put(SERIAL_FIELDS_METHODS, new Record((TSMap) methodFields))
            .put(SERIAL_FIELDS_DATA, new Record((TSMap) dataFields))
            .put(SERIAL_FIELDS_STATIC_METHODS, new Record((TSMap) staticMethodFields))
            .put(SERIAL_FIELDS_STATIC_DATA, new Record((TSMap) staticDataFields))
            .put(SERIAL_INHERITS, new Tuple((TSList) inherits)));
  }
}
