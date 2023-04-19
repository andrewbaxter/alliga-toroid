package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;

public class MortarDataGenericTupleField implements MortarRecordField {
  private final int offset;
  private final MortarDataTypeForGeneric type;

  public MortarDataGenericTupleField(int offset, MortarDataTypeForGeneric type) {
    this.offset = offset;
    this.type = type;
  }

  @Override
  public MortarRecordFieldstate recordfield_newFieldstate() {
    return new MortarDataGenericTupleFieldstate(offset, type.type_newTypestate());
  }

  @Override
  public AlligatorusType recordfield_asType() {
    return type;
  }
}
