package com.zarbosoft.merman.core.syntax.error;

import com.zarbosoft.merman.core.syntax.back.BackAtomSpec;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.merman.core.syntax.back.BaseBackArraySpec;

public class ArrayMultipleAtoms extends BaseKVError {
  public ArrayMultipleAtoms(BaseBackArraySpec spec, BackAtomSpec first, BackSpec second) {
    put("spec", spec);
    put("first", first);
    put("second", second);
  }

  @Override
  protected String description() {
    return "array element spec contains more than one atom spec";
  }
}
