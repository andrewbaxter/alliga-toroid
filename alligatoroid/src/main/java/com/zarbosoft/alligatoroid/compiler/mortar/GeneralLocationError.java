package com.zarbosoft.alligatoroid.compiler.mortar;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class GeneralLocationError extends Error.LocationError {
  public GeneralLocationError(Location location, String s) {
    super(
        location,
        new PreError() {
          @Override
          public String toString() {
            return s;
          }
        });
  }
}
