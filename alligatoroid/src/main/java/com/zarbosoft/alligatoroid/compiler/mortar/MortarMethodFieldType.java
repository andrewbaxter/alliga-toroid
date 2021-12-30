package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.model.Value;

public class MortarMethodFieldType implements SimpleValue, MortarHalfType {
  public final String name;
  public final String jbcDesc;
  /** Null if null */
  public final MortarHalfDataType returnType;

  public final boolean needsModule;

  public final MortarClass base;

  public MortarMethodFieldType(
      MortarClass base,
      String name,
      String jbcDesc,
      MortarHalfDataType returnType,
      boolean needsModule) {
    this.base = base;
    this.name = name;
    this.jbcDesc = jbcDesc;
    this.returnType = returnType;
    this.needsModule = needsModule;
  }

  @Override
  public Value asValue(MortarProtocode lower) {
    return new MortarMethodField(lower, this);
  }
}
