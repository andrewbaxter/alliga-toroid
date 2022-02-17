package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class CantSetStackValue extends Error.LocationError {
  public CantSetStackValue(Location location) {
    super(
        location,
        new PreError() {
          @Override
          public String toString() {
            return "You can't assign to an unbound value";
          }
        });
  }
}
