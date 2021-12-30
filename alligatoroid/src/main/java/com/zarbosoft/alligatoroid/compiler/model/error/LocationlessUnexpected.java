package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.rendaw.common.Format;

public class LocationlessUnexpected extends Error.LocationlessError {
    public final Throwable exception;

    public LocationlessUnexpected(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return Format.format("An unexpected error occurred while processing: %s", exception);
    }
}
