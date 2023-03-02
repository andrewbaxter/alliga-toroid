package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.mortar.Field;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;

public class MortarDataFieldProtoType implements MortarFieldProtoType {
  private final MortarObjectProtoType proto;
  private final ObjectMeta baseMeta;
  private final String name;

  public MortarDataFieldProtoType(MortarObjectProtoType proto, ObjectMeta baseMeta, String name) {
    this.proto = proto;
    this.baseMeta = baseMeta;
    this.name = name;
  }

  @Override
  public Field protoTypeNewField() {
    return new MortarDataField(proto.protoTypeNewType(), baseMeta, name);
  }
}
