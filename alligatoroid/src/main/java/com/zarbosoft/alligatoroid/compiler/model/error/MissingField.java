package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.TSList;

public class MissingField extends Error.LocationError {
  public MissingField(Location location, TSList<Object> fieldPath, Object first) {
    super(
        location,
        new PreError() {
          @Override
          public String toString() {
            return Format.format("Received type at %s is missing key %s", fieldPath, first);
          }
        });
  }
}
