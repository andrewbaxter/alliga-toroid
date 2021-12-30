package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class CacheUnexpectedPre extends Error.PreError {
    public final String cachePath;
    public final Throwable exception;

    public CacheUnexpectedPre(String cachePath, Throwable exception) {
        this.cachePath = cachePath;
        this.exception = exception;
    }

    @Override
    public Error toError(Location location) {
        return new CacheUnexpected(location, cachePath, exception);
    }
}
