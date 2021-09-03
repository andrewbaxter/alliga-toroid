package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializable;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.NullType;
import com.zarbosoft.alligatoroid.compiler.mortar.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.Tuple;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSMap;

public class JVMShallowMethodFieldType implements JVMType, GraphSerializable {
  public JVMBaseClassType base;
  public final JVMDataType returnType;
  public final String name;
  public final String jvmDesc;

  public JVMShallowMethodFieldType(
      JVMDataType returnType, String name, String jvmDesc) {
    this.returnType = returnType;
    this.name = name;
    this.jvmDesc = jvmDesc;
  }

  public static MethodSpecDetails specDetails(Record spec) {
    Object outRaw = spec.data.get("out");
    JVMDataType returnType = null;
    String returnDescriptor;
    if (outRaw == NullType.type) {
      returnDescriptor = JVMDescriptor.voidDescriptor();
    } else {
      JVMDataType inJvmType = (JVMDataType) outRaw;
      returnDescriptor = inJvmType.jvmDesc();
      returnType = inJvmType;
    }
    Object inRaw = spec.data.get("in");
    String[] argDescriptor;
    if (inRaw instanceof Tuple) {
      ROList<Object> inTuple = ((Tuple) inRaw).data;
      argDescriptor = new String[inTuple.size()];
      for (int i = 0; i < inTuple.size(); i++) {
        argDescriptor[i] = ((JVMDataType) inTuple.get(i)).jvmDesc();
      }
    } else {
      argDescriptor = new String[] {((JVMDataType) inRaw).jvmDesc()};
    }
    return new MethodSpecDetails(returnType, JVMDescriptor.func(returnDescriptor, argDescriptor));
  }

  public static JVMShallowMethodFieldType graphDeserialize(Record record) {
    return new JVMShallowMethodFieldType(
        (JVMDataType) record.data.get("returnType"),
        (String) record.data.get("name"),
        (String) record.data.get("jvmDesc"));
  }

  @Override
  public Value asValue(JVMProtocode code) {
    return new JVMMethodField(code, this);
  }

  @Override
  public Record graphSerialize() {
    return new Record(
        new TSMap<>(
            m -> {
              m.put("base", base)
                  .put("returnType", returnType)
                  .put("name", name)
                  .put("jvmDesc", jvmDesc);
            }));
  }

  public static class MethodSpecDetails {
    public final JVMDataType returnType;
    public final String jvmSigDesc;

    public MethodSpecDetails(JVMDataType returnType, String jvmSigDesc) {
      this.returnType = returnType;
      this.jvmSigDesc = jvmSigDesc;
    }
  }
}
