package com.zarbosoft.merman.syntax.error;

import com.zarbosoft.merman.editor.Path;

public class PluralInvalidAtLocation extends BaseKVError{

  public PluralInvalidAtLocation(Path typePath) {
        put("typePath", typePath);
  }

  @Override
  protected String description() {
    return "back field must describe single element (not subarray) here";
  }
}
