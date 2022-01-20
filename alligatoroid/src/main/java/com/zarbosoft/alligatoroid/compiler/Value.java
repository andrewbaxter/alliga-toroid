package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public interface Value {
  /**
   * Location or null
   *
   * @return
   */
  Location location();
}
