package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class NotRecordPair extends Error.LocationError {
  public NotRecordPair(Location location, String gotType) {
    super(location, new PreError(gotType));
  }

  private static class PreError extends Error.PreError {
    public final String gotType;

    public PreError(String gotType) {
      this.gotType = gotType;
    }

    @Override
    public String toString() {
      return String.format("Found %s instead of a record pair in record literal.", gotType);
    }
  }
}
