package com.zarbosoft.alligatoroid.compiler.mortar;

public class MortarDataGenericField implements MortarObjectField {
  private final MortarObjectInnerType parentInfo;
  private final String name;
  public final MortarDataType type;

  public MortarDataGenericField(
      MortarObjectInnerType parentInfo, String name, MortarDataType type) {
    this.parentInfo = parentInfo;
    this.name = name;
    this.type = type;
  }

  @Override
  public MortarObjectFieldstate field_newFieldstate() {
    return new MortarDataGenericFieldstate(parentInfo, name, type.type_newTypestate());
  }
}
