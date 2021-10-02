package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Module;
import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Map;

public class JVMExternClassType extends JVMBaseClassType implements GraphSerializable {
  public static final String SERIAL_NAME = "jvmName";
  public static final String SERIAL_FIELDS_METHODS = "methods";
  public static final String SERIAL_PRE_METHODS = "preMethods";
  public static final String SERIAL_FIELDS_DATA = "data";
  public static final String SERIAL_FIELDS_STATIC_METHODS = "staticMethods";
  public static final String SERIAL_PRE_STATIC_METHODS = "preStaticMethods";
  public static final String SERIAL_FIELDS_STATIC_DATA = "staticData";
  public static final String SERIAL_INHERITS = "inherits";
  public final TSMap<String, TSList<Record>> preMethodFields;
  public final TSMap<String, TSList<Record>> preStaticMethodFields;
  boolean finished;
  private boolean resolved;

  public JVMExternClassType(String jvmExternalClass) {
    super(
        jvmExternalClass,
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSMap<>(),
        new TSList<>());
    finished = false;
    preMethodFields = new TSMap<>();
    preStaticMethodFields = new TSMap<>();
  }

  private JVMExternClassType(
      String jvmExternalClass,
      TSMap<String, JVMDataType> dataFields,
      TSMap<ROTuple, JVMShallowMethodFieldType> methodFields,
      TSMap<String, JVMDataType> staticDataFields,
      TSMap<ROTuple, JVMShallowMethodFieldType> staticMethodFields,
      TSList<JVMBaseClassType> inherits,
      TSMap<String, TSList<Record>> preMethodFields,
      TSMap<String, TSList<Record>> preStaticMethodFields) {
    super(
        jvmExternalClass, dataFields, methodFields, staticDataFields, staticMethodFields, inherits);
    this.preMethodFields = preMethodFields;
    this.preStaticMethodFields = preStaticMethodFields;
    finished = true;
  }

  public static JVMExternClassType graphDeserialize(Record record) {
    JVMExternClassType out =
        new JVMExternClassType(
            (String) record.data.get(SERIAL_NAME),
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS_DATA)).data,
            new TSMap<>(),
            (TSMap) ((Record) record.data.get(SERIAL_FIELDS_STATIC_DATA)).data,
            new TSMap<>(),
            (TSList) ((Tuple) record.data.get(SERIAL_INHERITS)).data,
            new TSMap<>(),
            new TSMap<>());
    TSMap<String, Tuple> tuplePreMethods =
        (TSMap) ((Record) record.data.get(SERIAL_PRE_METHODS)).data;
    for (Map.Entry<String, Tuple> entry : tuplePreMethods.entries()) {
      TSList<Record> specs = new TSList<>();
      for (Object e : entry.getValue().data) {
        specs.add((Record) e);
      }
      out.preMethodFields.putNew(entry.getKey(), specs);
    }
    TSMap<String, Tuple> tuplePreStaticMethods =
        (TSMap) ((Record) record.data.get(SERIAL_PRE_STATIC_METHODS)).data;
    for (Map.Entry<String, Tuple> entry : tuplePreStaticMethods.entries()) {
      TSList<Record> specs = new TSList<>();
      for (Object e : entry.getValue().data) {
        specs.add((Record) e);
      }
      out.preStaticMethodFields.putNew(entry.getKey(), specs);
    }
    return out;
  }

  private void resolveOne(Module module, String name, Record spec) {
    JVMShallowMethodFieldType.MethodSpecDetails specDetails =
        JVMShallowMethodFieldType.methodSpecDetails(module, spec);
    JVMShallowMethodFieldType field =
        new JVMShallowMethodFieldType(specDetails.returnType, name, specDetails.jvmSigDesc);
    field.base = this;
    if (specDetails.isStatic) {
      staticMethodFields.put(ROTuple.create(name).append(specDetails.keyTuple), field);
    } else {
      methodFields.put(ROTuple.create(name).append(specDetails.keyTuple), field);
    }
  }

  @Override
  public void resolveMethods(Module module) {
    if (resolved) return;
    for (Map.Entry<String, TSList<Record>> entry : preStaticMethodFields.entries()) {
      for (Record spec : entry.getValue()) {
        resolveOne(module, entry.getKey(), spec);
      }
    }
    for (Map.Entry<String, TSList<Record>> entry : preMethodFields.entries()) {
      for (Record spec : entry.getValue()) {
        resolveOne(module, entry.getKey(), spec);
      }
    }
    resolved = true;
  }

  @Override
  public Record graphSerialize() {
    TSMap<String, Tuple> tupleStaticMethodFields = new TSMap<>();
    for (Map.Entry<String, TSList<Record>> entry : preStaticMethodFields.entries()) {
      tupleStaticMethodFields.put(entry.getKey(), new Tuple((ROList) entry.getValue()));
    }
    TSMap<String, Tuple> tupleMethodFields = new TSMap<>();
    for (Map.Entry<String, TSList<Record>> entry : preMethodFields.entries()) {
      tupleMethodFields.put(entry.getKey(), new Tuple((ROList) entry.getValue()));
    }
    return new Record(
        new TSMap<Object, Object>()
            .put(SERIAL_NAME, jvmName)
            .put(SERIAL_FIELDS_DATA, new Record((TSMap) dataFields))
            .put(SERIAL_FIELDS_STATIC_DATA, new Record((TSMap) staticDataFields))
            .put(SERIAL_INHERITS, new Tuple((TSList) inherits))
            .put(SERIAL_PRE_STATIC_METHODS, new Record((TSMap) tupleStaticMethodFields))
            .put(SERIAL_PRE_METHODS, new Record((TSMap) tupleMethodFields)));
  }
}
