package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Format;

public class DeserializeNotInteger extends Error.DeserializeError {
  public DeserializeNotInteger(LuxemPath path, String value) {
    super(path, new PreError(value));
  }

  private static class PreError extends Error.PreError {
    public final String value;

    public PreError(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return Format.format("Expected an integer in luxem but got [%s]", value);
    }
  }
}
