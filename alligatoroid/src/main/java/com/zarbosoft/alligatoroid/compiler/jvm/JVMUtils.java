package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JVMUtils {
  public static DataSpecDetails dataSpecDetails(Record spec) {
    return new DataSpecDetails(
        (JVMType) spec.data.get("type"),
        Objects.equals(spec.data.getOpt("protected"), true),
        Objects.equals(spec.data.getOpt("final"), true),
        Objects.equals(spec.data.getOpt("static"), true));
  }

  public static MethodSpecDetails methodSpecDetails(Record spec) {
    Object outRaw = spec.data.getOpt("out");
    JVMType returnType = null;
    JVMSharedDataDescriptor returnDescriptor;
    if (outRaw == null || outRaw == MortarNullType.type) {
      returnDescriptor = JVMSharedDataDescriptor.VOID;
    } else {
      JVMType inJvmType = (JVMType) outRaw;
      returnDescriptor = inJvmType.jvmDesc();
      returnType = inJvmType;
    }
    Object inRaw = spec.data.get("in");
    JVMSharedDataDescriptor[] argDescriptor;
    List argTupleData = new ArrayList();
    if (inRaw instanceof Tuple) {
      ROList<Object> inTuple = ((Tuple) inRaw).data;
      argDescriptor = new JVMSharedDataDescriptor[inTuple.size()];
      for (int i = 0; i < inTuple.size(); i++) {
        JVMType jvmDataType = (JVMType) inTuple.get(i);
        argDescriptor[i] = jvmDataType.jvmDesc();
        argTupleData.add(jvmDataType);
      }
    } else {
      JVMType jvmDataType = (JVMType) inRaw;
      argDescriptor = new JVMSharedDataDescriptor[] {jvmDataType.jvmDesc()};
      argTupleData.add(jvmDataType);
    }
    return new MethodSpecDetails(
        returnType,
        JVMSharedFuncDescriptor.fromParts(returnDescriptor, argDescriptor),
        new ROTuple(argTupleData),
        Objects.equals(spec.data.getOpt("protected"), true),
        Objects.equals(spec.data.getOpt("final"), true),
        Objects.equals(spec.data.getOpt("static"), true));
  }

  public static class DataSpecDetails {
    public final JVMType type;
    public final boolean isProtected;
    public final boolean isFinal;
    public final boolean isStatic;

    public DataSpecDetails(
            JVMType type, boolean isProtected, boolean isFinal, boolean isStatic) {
      this.type = type;
      this.isProtected = isProtected;
      this.isFinal = isFinal;
      this.isStatic = isStatic;
    }
  }

  public static class MethodSpecDetails {
    public final JVMType returnType;
    public final JVMSharedFuncDescriptor jvmSigDesc;
    public final ROTuple argTuple;
    public final boolean isProtected;
    public final boolean isFinal;
    public final boolean isStatic;

    public MethodSpecDetails(
        JVMType returnType,
        JVMSharedFuncDescriptor jvmSigDesc,
        ROTuple argTuple,
        boolean isProtected,
        boolean isFinal,
        boolean isStatic) {
      this.returnType = returnType;
      this.jvmSigDesc = jvmSigDesc;
      this.argTuple = argTuple;
      this.isProtected = isProtected;
      this.isFinal = isFinal;
      this.isStatic = isStatic;
    }
  }
}
