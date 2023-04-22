package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VoidValue;

public interface VoidType extends MortarType, MortarRecordFieldable {
  @Override
  default Value type_constAsValue(Object data) {
    return new VoidValue(type_newTypestate());
  }

  VoidTypestate type_newTypestate();
}
