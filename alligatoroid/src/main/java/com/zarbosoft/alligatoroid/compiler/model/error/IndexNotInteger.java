package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.MortarValue;
import com.zarbosoft.rendaw.common.Format;

public class IndexNotInteger extends Error.LocationError {
  public final MortarValue got;

  public IndexNotInteger(Location location, MortarValue got) {
    super(location);
    this.got = got;
  }

  @Override
  public String toString() {
    return Format.format("Indices must be integers but got [%s]", got);
  }
}
