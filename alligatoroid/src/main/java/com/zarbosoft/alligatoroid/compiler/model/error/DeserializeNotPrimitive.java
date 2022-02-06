package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;

public class DeserializeNotPrimitive extends Error.DeserializeError {
  public DeserializeNotPrimitive(LuxemPath path) {
    super(
        path,
        new PreError() {
          @Override
          public String toString() {
            return "A luxem primitive is not allowed at this location in the source";
          }
        });
  }
}
