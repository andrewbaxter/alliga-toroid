package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.jvm.value.base.JVMDataType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.value.autohalf.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.value.halftype.NullType;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JVMUtils {
  public static DataSpecDetails dataSpecDetails(Record spec) {
    return new DataSpecDetails(
        (JVMDataType) spec.data.get("type"),
        Objects.equals(spec.data.getOpt("protected"), true),
        Objects.equals(spec.data.getOpt("final"), true),
        Objects.equals(spec.data.getOpt("static"), true));
  }

  public static MethodSpecDetails methodSpecDetails(Record spec) {
    Object outRaw = spec.data.getOpt("out");
    JVMDataType returnType = null;
    String returnDescriptor;
    if (outRaw == null || outRaw == NullType.type) {
      returnDescriptor = JVMDescriptor.VOID_DESCRIPTOR;
    } else {
      JVMDataType inJvmType = (JVMDataType) outRaw;
      returnDescriptor = inJvmType.jvmDesc();
      returnType = inJvmType;
    }
    Object inRaw = spec.data.get("in");
    String[] argDescriptor;
    List argTupleData = new ArrayList();
    if (inRaw instanceof Tuple) {
      ROList<Object> inTuple = ((Tuple) inRaw).data;
      argDescriptor = new String[inTuple.size()];
      for (int i = 0; i < inTuple.size(); i++) {
        JVMDataType jvmDataType = (JVMDataType) inTuple.get(i);
        argDescriptor[i] = jvmDataType.jvmDesc();
        argTupleData.add(jvmDataType);
      }
    } else {
      JVMDataType jvmDataType = (JVMDataType) inRaw;
      argDescriptor = new String[] {jvmDataType.jvmDesc()};
      argTupleData.add(jvmDataType);
    }
    return new MethodSpecDetails(
        returnType,
        JVMDescriptor.func(returnDescriptor, argDescriptor),
        new ROTuple(argTupleData),
        Objects.equals(spec.data.getOpt("protected"), true),
        Objects.equals(spec.data.getOpt("final"), true),
        Objects.equals(spec.data.getOpt("static"), true));
  }

  public static class DataSpecDetails {
    public final JVMDataType type;
    public final boolean isProtected;
    public final boolean isFinal;
    public final boolean isStatic;

    public DataSpecDetails(
        JVMDataType type, boolean isProtected, boolean isFinal, boolean isStatic) {
      this.type = type;
      this.isProtected = isProtected;
      this.isFinal = isFinal;
      this.isStatic = isStatic;
    }
  }

  public static class MethodSpecDetails {
    public final JVMDataType returnType;
    public final String jvmSigDesc;
    public final ROTuple keyTuple;
    public final boolean isProtected;
    public final boolean isFinal;
    public final boolean isStatic;

    public MethodSpecDetails(
        JVMDataType returnType,
        String jvmSigDesc,
        ROTuple keyTuple,
        boolean isProtected,
        boolean isFinal,
        boolean isStatic) {
      this.returnType = returnType;
      this.jvmSigDesc = jvmSigDesc;
      this.keyTuple = keyTuple;
      this.isProtected = isProtected;
      this.isFinal = isFinal;
      this.isStatic = isStatic;
    }
  }
}
