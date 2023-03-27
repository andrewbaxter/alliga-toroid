package com.zarbosoft.alligatoroid.compiler.mortar.halftypes;

import com.zarbosoft.alligatoroid.compiler.mortar.Field;
import com.zarbosoft.alligatoroid.compiler.mortar.builtinother.ObjectMeta;

public class MortarDataProtofield implements MortarProtofield {
  private final MortarDataPrototype proto;
  private final ObjectMeta baseMeta;
  private final String name;

  public MortarDataProtofield(MortarDataPrototype proto, ObjectMeta baseMeta, String name) {
    this.proto = proto;
    this.baseMeta = baseMeta;
    this.name = name;
  }

  @Override
  public Field protofield_newField() {
    return new MortarDataField(proto.prototype_newType(), baseMeta, name);
  }
}
