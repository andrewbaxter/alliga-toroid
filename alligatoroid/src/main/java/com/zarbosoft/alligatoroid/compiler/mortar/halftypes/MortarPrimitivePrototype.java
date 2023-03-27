package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaBytecode;
import com.zarbosoft.alligatoroid.compiler.jvmshared.JavaDataDescriptor;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;

public class MortarPrimitivePrototype implements MortarDataPrototype {
  public static MortarPrimitivePrototype intPrototype =
      new MortarPrimitivePrototype(MortarIntType.type);
  public static MortarPrimitivePrototype boolPrototype =
      new MortarPrimitivePrototype(MortarBoolType.type);
  public static MortarPrimitivePrototype stringPrototype =
      new MortarPrimitivePrototype(MortarStringType.type);
  public static MortarPrimitivePrototype bytePrototype =
      new MortarPrimitivePrototype(MortarByteType.type);
  public static MortarPrimitivePrototype bytesPrototype =
      new MortarPrimitivePrototype(MortarBytesType.type);
  public final MortarPrimitiveType type;

  private MortarPrimitivePrototype(MortarPrimitiveType type) {
    this.type = type;
  }

  @Override
  public JavaDataDescriptor prototype_jvmDesc() {
    return type.type_jvmDesc();
  }

  @Override
  public Value prototype_stackAsValue(JavaBytecode code) {
    return type.type_stackAsValue(code);
  }

  @Override
  public JavaBytecode prototype_returnBytecode() {
    return type.type_returnBytecode();
  }

  @Override
  public MortarDataType prototype_newType() {
    return type;
  }

  @Override
  public MortarProtofield prototype_newProtofield(ObjectMeta baseMeta, String name) {
    return new MortarDataProtofield(this, baseMeta, name);
  }
}
