package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;

public class MortarDataGenericField implements MortarObjectField {
  private final MortarObjectInnerType parentInfo;
  private final String name;
  public final MortarDataTypeForGeneric type;

  public MortarDataGenericField(
      MortarObjectInnerType parentInfo, String name, MortarDataTypeForGeneric type) {
    this.parentInfo = parentInfo;
    this.name = name;
    this.type = type;
  }

  @Override
  public MortarObjectFieldstate field_newFieldstate() {
    return new MortarDataGenericFieldstate(parentInfo, name, type.type_newTypestate());
  }

  @Override
  public AlligatorusType field_asType() {
    return type;
  }
}
