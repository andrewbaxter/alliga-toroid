package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarTupleTypestate;
import com.zarbosoft.rendaw.common.TSList;

public class MortarDeferredCodeAccessTupleField implements MortarDeferredCode {
  public final MortarDeferredCode base;
  public final int field;
  public final JavaDataDescriptor type;

  public MortarDeferredCodeAccessTupleField(
      MortarDeferredCode base, int field, JavaDataDescriptor type) {
    this.base = base;
    this.field = field;
    this.type = type;
  }

  @Override
  public JavaBytecode drop() {
    return JavaBytecodeUtils.seq().add(base.drop());
  }

  @Override
  public JavaBytecode consume() {
    JavaBytecodeSequence out =
        JavaBytecodeUtils.seq()
            .add(base.consume())
            .add(JavaBytecodeUtils.literalIntShortByte(field))
            .add(
                JavaBytecodeUtils.callMethod(
                    -1,
                    MortarTupleTypestate.JVMNAME,
                    "get",
                    JavaMethodDescriptor.fromParts(
                        JavaDataDescriptor.OBJECT, new TSList<>(JavaDataDescriptor.INT))))
            .add(JavaBytecodeUtils.cast(type));
    return out;
  }

  @Override
  public JavaBytecode set(JavaBytecode value) {
    JavaBytecodeSequence out =
        JavaBytecodeUtils.seq()
            .add(base.consume())
            .add(JavaBytecodeUtils.literalIntShortByte(field))
            .add(value)
            .add(
                JavaBytecodeUtils.callMethod(
                    -1,
                    MortarTupleTypestate.JVMNAME,
                    "set",
                    JavaMethodDescriptor.fromParts(
                        JavaDataDescriptor.OBJECT,
                        new TSList<>(JavaDataDescriptor.INT, JavaDataDescriptor.OBJECT))));
    return out;
  }
}
