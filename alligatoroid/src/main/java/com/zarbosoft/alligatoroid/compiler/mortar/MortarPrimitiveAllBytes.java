package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Global;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeInstructionObj;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import org.objectweb.asm.tree.IntInsnNode;

import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.T_BYTE;

public class MortarPrimitiveAllBytes implements MortarPrimitiveAll.Inner {
  public static final MortarPrimitiveAllBytes instance = new MortarPrimitiveAllBytes();

  private MortarPrimitiveAllBytes() {}

  @Override
  public JavaDataDescriptor jvmDesc() {
    return Global.DESC_BYTE_ARRAY;
  }

  @Override
  public JavaBytecode returnBytecode() {
    return Global.JBC_returnObj;
  }

  @Override
  public JavaBytecode storeBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.storeObj(key);
  }

  @Override
  public JavaBytecode loadBytecode(JavaBytecodeBindingKey key) {
    return JavaBytecodeUtils.loadObj(key);
  }

  @Override
  public JavaBytecode arrayLoadBytecode() {
    return Global.JBC_ARRAY_LOAD_OBJ;
  }

  @Override
  public JavaBytecode arrayStoreBytecode() {
    return Global.JBC_ARRAY_STORE_OBJ;
  }

  @Override
  public JavaBytecode literalBytecode(Object constData) {
    final JavaBytecodeSequence out = new JavaBytecodeSequence();
    byte[] arr = (byte[]) constData;
    out.add(JavaBytecodeUtils.literalIntShortByte(arr.length));
    out.add(new JavaBytecodeInstructionObj(new IntInsnNode(NEWARRAY, T_BYTE)));
    for (int i = 0; i < arr.length; i++) {
      final byte b = arr[i];
      out.add(Global.JBC_DUP);
      out.add(JavaBytecodeUtils.literalIntShortByte(i));
      out.add(JavaBytecodeUtils.literalIntShortByte(b));
      out.add(Global.JBC_ARRAY_STORE_INT);
    }
    return out;
  }

  @Override
  public JavaBytecode fromObj() {
    return JavaBytecodeUtils.cast(jvmDesc());
  }

  @Override
  public JavaBytecode toObj() {
    return null;
  }
}
