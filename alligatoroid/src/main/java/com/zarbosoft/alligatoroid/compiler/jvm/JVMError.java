package com.zarbosoft.alligatoroid.compiler.jvm;

import com.zarbosoft.alligatoroid.compiler.Error;
import com.zarbosoft.alligatoroid.compiler.Location;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROTuple;
import com.zarbosoft.rendaw.common.TSMap;

import static com.zarbosoft.alligatoroid.compiler.Error.DESCRIPTION_KEY;

public class JVMError {
  public static Error noMethodField(Location location, String name, ROTuple arguments) {
    return new Error(
        "no_matching_method",
        new TSMap<String, Object>()
            .put("location", location)
            .put("name", name)
            .put("arguments", arguments)
            .put(DESCRIPTION_KEY, Format.format("No method named %s with these arguments", name)));
  }

  public static Error noDataField(Location location, String name) {
    return new Error(
        "no_matching_data",
        new TSMap<String, Object>()
            .put("location", location)
            .put("name", name)
            .put(DESCRIPTION_KEY, Format.format("No data field named %s", name)));
  }
}
