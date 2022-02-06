package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.ErrorValue;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;

public class WrongType extends Error.LocationError {
  public WrongType(Location location, String got, String expected) {
    super(location, new PreError(got, expected));
  }

  private static class PreError extends Error.PreError {
    public final String expected;
    public final String got;

    public PreError(String got, String expected) {
      this.expected = expected;
      this.got = got;
    }

    @Override
    public String toString() {
      return Format.format("Expected [%s] but got value [%s]", expected, got);
    }
  }
}
