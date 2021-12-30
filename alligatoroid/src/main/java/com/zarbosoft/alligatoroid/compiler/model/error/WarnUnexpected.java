package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.rendaw.common.Format;

public class WarnUnexpected extends Error.LocationlessError {
    public final Throwable exception;
    public final String path;

    public WarnUnexpected(String path, Throwable exception) {
        this.exception = exception;
        this.path = path;
    }

    @Override
    public String toString() {
        return Format.format(
                "An unexpected error occurred while processing [%s]: %s", path, exception);
    }
}
