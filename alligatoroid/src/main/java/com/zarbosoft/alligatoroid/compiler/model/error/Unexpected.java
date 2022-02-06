package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;

import java.util.concurrent.ExecutionException;

public class Unexpected extends Error.LocationError {
  public Unexpected(Location location, Throwable exception) {
    super(location, new PreError(exception));
    if (exception instanceof PreError || exception instanceof ExecutionException)
      throw new Assertion();
  }

  private static class PreError extends Error.PreError {
    private final Throwable exception;

    public PreError(Throwable exception) {
      this.exception = exception;
    }

    @Override
    public String toString() {
      return Format.format("An unexpected error occurred while processing: %s", exception);
    }
  }
}
