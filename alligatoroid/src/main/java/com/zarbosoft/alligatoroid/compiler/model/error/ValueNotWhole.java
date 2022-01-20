package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.Value;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class ValueNotWhole extends Error.LocationError {
  public final Value value;

  public ValueNotWhole(Location location, Value value) {
    super(location);
    this.value = value;
  }

  @Override
  public String toString() {
    return "This value needs to be known completely in phase 1 for use here";
  }
}
