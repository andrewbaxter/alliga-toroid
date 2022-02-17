package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class ValueNotWhole extends Error.LocationError {
  public ValueNotWhole(Location location) {
    super(location, new PreError());
  }

  private static class PreError extends Error.PreError {
    @Override
    public String toString() {
      return "This value needs to be known completely in phase 1 for use here";
    }
  }
}
