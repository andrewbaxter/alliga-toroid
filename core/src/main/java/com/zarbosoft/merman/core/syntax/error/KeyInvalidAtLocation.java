package com.zarbosoft.merman.core.syntax.error;

import com.zarbosoft.merman.core.SyntaxPath;

public class KeyInvalidAtLocation extends BaseKVError{

  public KeyInvalidAtLocation(SyntaxPath backPath, SyntaxPath keyPath) {
        put("path", backPath).put("keyPath", keyPath);
  }

  @Override
  protected String description() {
    return "back field must not describe key (but found direct key child)";
  }
}
