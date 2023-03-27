package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarDataVariableValue;
import com.zarbosoft.rendaw.common.Format;

public class IndexNotInteger extends Error.LocationError {
  public IndexNotInteger(Location location, MortarDataVariableValue got) {
    super(location, new PreError(got));
  }

  private static class PreError extends Error.PreError {
    public final MortarDataVariableValue got;

    public PreError(MortarDataVariableValue got) {
      this.got = got;
    }

    @Override
    public String toString() {
      return Format.format("Indices must be integers but got [%s]", got);
    }
  }
}
