package com.zarbosoft.merman.core.syntax.error;

import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.syntax.back.BackSpecData;

public class BackFieldWrongType extends BaseKVError {
  public BackFieldWrongType(SyntaxPath typePath, String field, BackSpecData found, String expected) {
            put("typePath", typePath);
            put("field", field);
            put("found", found);
            put("expected", expected);
  }

  @Override
  protected String description() {
    return "back field has incompatible type";
  }
}
