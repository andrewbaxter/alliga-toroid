package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;

public class DeserializeNotTyped extends Error.DeserializeError {
  public DeserializeNotTyped(LuxemPath path) {
    super(
        path,
        new PreError() {
          @Override
          public String toString() {
            return "A luxem type is not allowed at this location in the source";
          }
        });
  }
}
