package com.zarbosoft.merman.core.syntax.error;

import com.zarbosoft.merman.core.SyntaxPath;

public class NonKeyInvalidAtLocation extends BaseKVError{

  public NonKeyInvalidAtLocation(SyntaxPath backPath, SyntaxPath nonKeyPath) {
        put("path", backPath).put("nonKeyPath", nonKeyPath);
  }

  @Override
  protected String description() {
    return "record back field must describe key (but found direct non-key child)";
  }
}
