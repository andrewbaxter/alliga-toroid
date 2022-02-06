package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;

public class DeserializePairTooManyValues extends Error.DeserializeError {
  public DeserializePairTooManyValues(LuxemPath backPath) {
    super(
        backPath,
        new PreError() {
          @Override
          public String toString() {
            return "This value is a 2-element array, but found more than 2 elements.";
          }
        });
  }
}
