package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Format;

public class DeserializeMissingField extends Error.DeserializeError {
  public DeserializeMissingField(LuxemPath path, String type, String field) {
    super(path, new PreError(type, field));
  }

  private static class PreError extends Error.PreError {
    public final String type;
    public final String field;

    public PreError(String type, String field) {
      this.type = type;
      this.field = field;
    }

    @Override
    public String toString() {
      return Format.format(
          "Luxem type [%s] requires a field [%s] but a value was not provided", type, field);
    }
  }
}
