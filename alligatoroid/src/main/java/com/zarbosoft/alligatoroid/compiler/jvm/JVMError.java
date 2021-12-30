package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.error.Unexpected;
import com.zarbosoft.rendaw.common.Format;

public class JVMError {
  public static Error noMethodField(Location location, String name) {
    return new Unexpected(
        location,
        new RuntimeException(Format.format("No method named %s with these arguments", name)));
  }

  public static Error noDataField(Location location, String name) {
    return new Unexpected(
        location, new RuntimeException(Format.format("No data field named %s", name)));
  }
}
