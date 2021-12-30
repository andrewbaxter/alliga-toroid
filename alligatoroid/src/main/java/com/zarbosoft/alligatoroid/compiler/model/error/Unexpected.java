package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class Unexpected extends Error.LocationError {
    public final Throwable exception;

    public Unexpected(Location location, Throwable exception) {
        super(location);
        this.exception = exception;
    }

    @Override
    public String toString() {
        return Format.format("An unexpected error occurred while processing: %s", exception);
    }
}
