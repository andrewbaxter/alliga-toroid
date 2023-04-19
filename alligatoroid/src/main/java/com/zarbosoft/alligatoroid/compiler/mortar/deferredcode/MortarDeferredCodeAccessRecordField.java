package com.zarbosoft.alligatoroid.compiler.mortar.deferredcode;

import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarRecordTypestate;
import com.zarbosoft.rendaw.common.TSList;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;

public class MortarDeferredCodeAccessRecordField implements MortarDeferredCode {
  public final MortarDeferredCode base;
  public final int field;
  private final JavaBytecode box;
  private final JavaBytecode unbox;

  public MortarDeferredCodeAccessRecordField(
      MortarDeferredCode base, int field, JavaBytecode box, JavaBytecode unbox) {
    this.base = base;
    this.field = field;
    this.box = box;
    this.unbox = unbox;
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
            .add(JavaBytecodeUtils.inst(AALOAD))
            .add(unbox);
    return out;
  }

  @Override
  public JavaBytecode set(JavaBytecode valueCode) {
    JavaBytecodeSequence out =
        JavaBytecodeUtils.seq()
            .add(base.consume())
            .add(valueCode)
            .add(box)
            .add(JavaBytecodeUtils.literalIntShortByte(field))
            .add(JavaBytecodeUtils.inst(AASTORE));
    return out;
  }
}
