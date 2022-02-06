package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class NoField extends Error.LocationError {
  public NoField(Location location, Object field) {
    super(location, new PreError(field));
  }

  private static class PreError extends Error.PreError {
    public final Object field;

    public PreError(Object field) {
      this.field = field;
    }

    @Override
    public String toString() {
      return Format.format("Field [%s] doesn't exist", field);
    }
  }
}
