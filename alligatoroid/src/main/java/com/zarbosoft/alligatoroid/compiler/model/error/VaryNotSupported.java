package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class VaryNotSupported extends Error.LocationError {
  public VaryNotSupported(Location location) {
    super(
        location,
        new PreError() {
          @Override
          public String toString() {
            return "This value cannot be varied";
          }
        });
  }
}
