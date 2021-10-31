package com.zarbosoft.merman.core.syntax.error;

import com.zarbosoft.merman.core.SyntaxPath;

public class PluralInvalidAtLocation extends BaseKVError{

  public PluralInvalidAtLocation(SyntaxPath backPath, SyntaxPath pluralPath) {
        put("path", backPath).put("pluralPath", pluralPath);
  }

  @Override
  protected String description() {
    return "back field must describe single element (not subarray) here";
  }
}
