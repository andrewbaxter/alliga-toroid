package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.TargetModuleContext;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class WrongTarget extends Error.LocationError {
  public WrongTarget(
      Location location, TargetModuleContext.Id expected, TargetModuleContext.Id got) {
    super(
        location,
        new PreError() {
          @Override
          public String toString() {
            return Format.format(
                "This can only be done in target %s, but currently evaluating in target %s",
                expected, got);
          }
        });
  }
}
