package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

public class WrongType extends Error.LocationError {
  public WrongType(Location location, ROList<Object> path, String got, String expected) {
    super(location, new PreError(path, got, expected));
  }

  private static class PreError extends Error.PreError {
    public final String expected;
    public final String got;
    private final ROList<Object> path;

    public PreError(ROList<Object> path, String got, String expected) {
      this.path = path;
      this.expected = expected;
      this.got = got;
    }

    @Override
    public String toString() {
      return Format.format("Expected [%s] but got value [%s] at [%s]", expected, got, path);
    }
  }
}
