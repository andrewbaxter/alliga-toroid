package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class CallNotSupported extends Error.LocationError {
  public CallNotSupported(Location location) {
    super(
        location,
        new PreError() {
          @Override
          public String toString() {
            return "This value cannot be called";
          }
        });
  }
}
