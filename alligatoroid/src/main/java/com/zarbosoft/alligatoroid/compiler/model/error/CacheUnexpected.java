package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class CacheUnexpected extends CacheFileError {
    public final Throwable exception;

    public CacheUnexpected(Location location, String cachePath, Throwable exception) {
        super(location, cachePath);
        this.exception = exception;
    }

    @Override
    public String toString() {
        return Format.format("An unexpected error occurred while loading cache file: %s", exception);
    }
}
