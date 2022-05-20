package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.halftypes.MortarRecordType;
import com.zarbosoft.rendaw.common.TSList;

public class MortarDeferredCodeAccessRecordField implements MortarDeferredCode {
  public final MortarDeferredCode base;
  public final JavaBytecode field;
  public final JavaDataDescriptor type;

  public MortarDeferredCodeAccessRecordField(
      MortarDeferredCode base, JavaBytecode field, JavaDataDescriptor type) {
    this.base = base;
    this.field = field;
    this.type = type;
  }

  @Override
  public JavaBytecode drop() {
    return JavaBytecodeUtils.seq().add(base.drop()).add(field).add(JavaBytecodeUtils.pop);
  }

  @Override
  public JavaBytecode consume() {
    JavaBytecodeSequence out =
        JavaBytecodeUtils.seq()
            .add(base.consume())
            .add(field)
            .add(
                JavaBytecodeUtils.callMethod(
                    -1,
                    MortarRecordType.JVMNAME,
                    "get",
                    JavaMethodDescriptor.fromParts(
                        JavaDataDescriptor.OBJECT, new TSList<>(JavaDataDescriptor.OBJECT))))
            .add(JavaBytecodeUtils.cast(type));
    return out;
  }

  @Override
  public JavaBytecode set(JavaBytecode value) {
    JavaBytecodeSequence out =
        JavaBytecodeUtils.seq()
            .add(base.consume())
            .add(field)
            .add(value)
            .add(
                JavaBytecodeUtils.callMethod(
                    -1,
                    MortarRecordType.JVMNAME,
                    "set",
                    JavaMethodDescriptor.fromParts(
                        JavaDataDescriptor.OBJECT,
                        new TSList<>(JavaDataDescriptor.OBJECT, JavaDataDescriptor.OBJECT))));
    return out;
  }
}
