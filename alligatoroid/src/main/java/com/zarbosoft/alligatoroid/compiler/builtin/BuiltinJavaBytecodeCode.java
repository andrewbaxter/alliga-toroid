package com.zarbosoft.alligatoroid.compiler.builtin;

import com.zarbosoft.alligatoroid.compiler.mortar.StaticAutogen;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeBindingKey;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeSequence;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeStoreLoad;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecodeUtils;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaInternalName;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaMethodDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarObjectImplType;

import static com.zarbosoft.alligatoroid.compiler.Global.JBC_ARRAY_LOAD_INT;
import static com.zarbosoft.alligatoroid.compiler.Global.JBC_ARRAY_STORE_INT;
import static com.zarbosoft.alligatoroid.compiler.Global.JBC_ARRAY_LOAD_OBJ;
import static com.zarbosoft.alligatoroid.compiler.Global.JBC_ARRAY_STORE_OBJ;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;

@StaticAutogen.BuiltinAggregate
public class BuiltinJavaBytecodeCode {
  public final MortarObjectImplType type = StaticAutogen.autoMortarObjectTypes.get(JavaBytecode.class);

  public static JavaBytecode nop() {
    return null;
  }

  public static JavaBytecodeSequence seq() {
    return new JavaBytecodeSequence();
  }

  public static JavaBytecode _int(int value) {
    return JavaBytecodeUtils.literalIntShortByte(value);
  }

  public static JavaBytecode _bool(boolean value) {
    return JavaBytecodeUtils.bool_(value);
  }

  public static JavaBytecode _string(String value) {
    return JavaBytecodeUtils.literalString(value);
  }

  public static JavaBytecode loadObject(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(ALOAD, key);
  }

  public static JavaBytecode storeObject(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(ASTORE, key);
  }

  public static JavaBytecode loadArrayObject() {
    return JBC_ARRAY_LOAD_OBJ;
  }

  public static JavaBytecode storeArrayObject() {
    return JBC_ARRAY_STORE_OBJ;
  }

  public static JavaBytecode loadInt(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(ILOAD, key);
  }

  public static JavaBytecode storeInt(JavaBytecodeBindingKey key) {
    return JavaBytecodeStoreLoad.create(ISTORE, key);
  }

  public static JavaBytecode loadArrayInt() {
    return JBC_ARRAY_LOAD_INT;
  }

  public static JavaBytecode storeArrayInt() {
    return JBC_ARRAY_STORE_INT;
  }

  public static JavaBytecode loadStaticField(
      JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    return JavaBytecodeUtils.accessStaticField(-1, klass, field, fieldDesc);
  }

  public static JavaBytecode storeStaticField(
      JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    return JavaBytecodeUtils.setStaticField(-1, klass, field, fieldDesc);
  }

  public static JavaBytecode loadField(
      JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    return JavaBytecodeUtils.accessField(-1, klass, field, fieldDesc);
  }

  public static JavaBytecode storeField(
      JavaInternalName klass, String field, JavaDataDescriptor fieldDesc) {
    return JavaBytecodeUtils.setField(-1, klass, field, fieldDesc);
  }

  public static JavaBytecode callStaticMethod(
      JavaInternalName base, String method, JavaMethodDescriptor methodDescriptor) {
    return JavaBytecodeUtils.callStaticMethod(-1, base, method, methodDescriptor);
  }

  public static JavaBytecode callMethod(
      JavaInternalName base, String method, JavaMethodDescriptor methodDescriptor) {
    return JavaBytecodeUtils.callMethod(-1, base, method, methodDescriptor);
  }

  public static JavaBytecode instantiate(
      JavaInternalName klass, JavaMethodDescriptor desc, JavaBytecode arguments) {
    return JavaBytecodeUtils.instantiate(-1, klass, desc, arguments);
  }

  public static JavaBytecode callConstructor(
      JavaInternalName klass, JavaMethodDescriptor methodDescriptor) {
    return JavaBytecodeUtils.callConstructor(-1, klass, methodDescriptor);
  }

  public static JavaBytecode cast(JavaDataDescriptor toType) {
    return JavaBytecodeUtils.cast(toType);
  }
}
