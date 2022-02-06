package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;

public class DeserializeNotRecord extends Error.DeserializeError {
  public DeserializeNotRecord(LuxemPath path) {
    super(
        path,
        new PreError() {
          @Override
          public String toString() {
            return "A luxem record is not allowed at this location in the source";
          }
        });
  }
}
