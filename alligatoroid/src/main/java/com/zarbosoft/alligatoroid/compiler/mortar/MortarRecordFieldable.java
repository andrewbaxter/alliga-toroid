package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.AlligatorusType;

public interface MortarRecordFieldable extends AlligatorusType {

  MortarRecordField newTupleField(int offset);
}
