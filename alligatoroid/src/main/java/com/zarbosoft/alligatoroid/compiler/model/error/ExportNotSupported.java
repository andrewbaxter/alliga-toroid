package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class ExportNotSupported extends Error.LocationError {
  public ExportNotSupported(Location location) {
    super(
        location,
        new PreError() {
          @Override
          public String toString() {
            return "This value cannot be exported";
          }
        });
  }
}
