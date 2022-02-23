package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.inout.graph.Exportable;
import com.zarbosoft.alligatoroid.compiler.inout.utils.graphauto.AutoBuiltinExportable;
import com.zarbosoft.alligatoroid.compiler.jvm.halftypes.JVMType;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JVMSharedFuncDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Record;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.Tuple;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarNullType;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JVMUtils {
  public static DataSpecDetailsAttributes dataSpecDetailsAttributes(Record spec) {
    return DataSpecDetailsAttributes.create(
        Objects.equals(spec.data.getOpt("protected"), true),
        Objects.equals(spec.data.getOpt("final"), true),
        Objects.equals(spec.data.getOpt("static"), true));
  }

  public static DataSpecDetails dataSpecDetails(Record spec) {
    return new DataSpecDetails((JVMType) spec.data.get("type"), dataSpecDetailsAttributes(spec));
  }

  public static MethodSpecDetailsAttributes methodSpecDetailsAttributes(Record spec) {
    return MethodSpecDetailsAttributes.create(
        Objects.equals(spec.data.getOpt("protected"), true),
        Objects.equals(spec.data.getOpt("final"), true),
        Objects.equals(spec.data.getOpt("static"), true));
  }

  public static MethodSpecDetails methodSpecDetails(Record spec) {
    Object outRaw = spec.data.getOpt("out");
    Object inRaw0 = spec.data.get("in");
    final TSList<JVMType> inRaw = new TSList<>();
    if (inRaw0 instanceof Tuple) {
      for (Object e : ((Tuple) inRaw0).data) {
        inRaw.add((JVMType) e);
      }
    } else {
      inRaw.add((JVMType) inRaw0);
    }
    return methodSpecDetails(outRaw, inRaw, methodSpecDetailsAttributes(spec));
  }

  public static MethodSpecDetails methodSpecDetails(
      Object outRaw, TSList<JVMType> inTuple, MethodSpecDetailsAttributes attributes) {
    JVMType returnType = null;
    JVMSharedDataDescriptor returnDescriptor;
    if (outRaw == null || outRaw == MortarNullType.type) {
      returnDescriptor = JVMSharedDataDescriptor.VOID;
    } else {
      JVMType inJvmType = (JVMType) outRaw;
      returnDescriptor = inJvmType.jvmDesc();
      returnType = inJvmType;
    }
    JVMSharedDataDescriptor[] argDescriptor;
    List argTupleData = new ArrayList();
    argDescriptor = new JVMSharedDataDescriptor[inTuple.size()];
    for (int i = 0; i < inTuple.size(); i++) {
      Object jvmDataType = inTuple.get(i);
      if (jvmDataType instanceof JVMType) argDescriptor[i] = ((JVMType) jvmDataType).jvmDesc();
      else argDescriptor[i] = JVMSharedDataDescriptor.VOID;
      argTupleData.add(jvmDataType);
    }
    return new MethodSpecDetails(
        returnType,
        JVMSharedFuncDescriptor.fromParts(returnDescriptor, argDescriptor),
        new ROTuple(argTupleData),
        attributes);
  }

  public static class DataSpecDetailsAttributes implements AutoBuiltinExportable {
    @Exportable.Param public boolean isProtected;
    @Exportable.Param public boolean isFinal;
    @Exportable.Param public boolean isStatic;

    public static DataSpecDetailsAttributes create(
        boolean isProtected, boolean isFinal, boolean isStatic) {
      final DataSpecDetailsAttributes out = new DataSpecDetailsAttributes();
      out.isProtected = isProtected;
      out.isFinal = isFinal;
      out.isStatic = isStatic;
      return out;
    }
  }

  public static class DataSpecDetails {
    public final JVMType type;
    public final DataSpecDetailsAttributes attributes;

    public DataSpecDetails(JVMType type, DataSpecDetailsAttributes attributes) {
      this.type = type;
      this.attributes = attributes;
    }
  }

  public static class MethodSpecDetailsAttributes implements AutoBuiltinExportable {
    @Param public boolean isProtected;
    @Param public boolean isFinal;
    @Param public boolean isStatic;

    public static MethodSpecDetailsAttributes create(
        boolean isProtected, boolean isFinal, boolean isStatic) {
      final MethodSpecDetailsAttributes out = new MethodSpecDetailsAttributes();
      out.isProtected = isProtected;
      out.isFinal = isFinal;
      out.isStatic = isStatic;
      return out;
    }
  }

  public static class MethodSpecDetails {
    public final JVMType returnType;
    public final JVMSharedFuncDescriptor jvmSigDesc;
    public final ROTuple argTuple;
    public final MethodSpecDetailsAttributes attributes;

    public MethodSpecDetails(
        JVMType returnType,
        JVMSharedFuncDescriptor jvmSigDesc,
        ROTuple argTuple,
        MethodSpecDetailsAttributes attributes) {
      this.returnType = returnType;
      this.jvmSigDesc = jvmSigDesc;
      this.argTuple = argTuple;
      this.attributes = attributes;
    }
  }
}
