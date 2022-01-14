package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.value.Value;
import com.zarbosoft.rendaw.common.Format;

public class IndexNotInteger extends Error.LocationError {
  public final Value got;

  public IndexNotInteger(Location location, Value got) {
    super(location);
    this.got = got;
  }

  @Override
  public String toString() {
    return Format.format("Indices must be integers but got [%s]", got);
  }
}
