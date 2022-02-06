package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.VariableDataStackValue;
import com.zarbosoft.rendaw.common.Format;

public class IndexNotInteger extends Error.LocationError {
  public IndexNotInteger(Location location, VariableDataStackValue got) {
    super(location, new PreError(got));
  }

  private static class PreError extends Error.PreError {
    public final VariableDataStackValue got;

    public PreError(VariableDataStackValue got) {
      this.got = got;
    }

    @Override
    public String toString() {
      return Format.format("Indices must be integers but got [%s]", got);
    }
  }
}
