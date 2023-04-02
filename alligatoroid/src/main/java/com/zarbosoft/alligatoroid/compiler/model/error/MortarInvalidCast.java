package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.mortar.MortarDataType;
import com.zarbosoft.rendaw.common.Format;

public class MortarInvalidCast extends Error.LocationError {
  public MortarInvalidCast(Location location, MortarDataType prototype) {
    super(
        location,
        new PreError() {
          @Override
          public String toString() {
            return Format.format("This value cannot be cast as %s", prototype);
          }
        });
  }
}
