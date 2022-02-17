package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.TSList;

public class ExtraField extends Error.LocationError {
  public ExtraField(Location location, TSList<Object> path, Object otherKey) {
    super(
        location,
        new Error.PreError() {
          @Override
          public String toString() {
            return Format.format("Received unknown field %s at %s", otherKey, path);
          }
        });
  }
}
