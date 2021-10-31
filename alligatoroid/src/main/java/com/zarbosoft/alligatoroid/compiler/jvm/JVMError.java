package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROTuple;

public class JVMError {
  public static Error noMethodField(Location location, String name) {
    return new Error.Unexpected(
        location,
        new RuntimeException(Format.format("No method named %s with these arguments", name)));
  }

  public static Error noDataField(Location location, String name) {
    return new Error.Unexpected(
        location, new RuntimeException(Format.format("No data field named %s", name)));
  }
}
