package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public abstract class CacheFileError extends Error.LocationError {
    public final String cachePath;

    public CacheFileError(Location location, String cachePath) {
        super(location);
        this.cachePath = cachePath;
    }

    @Override
    public <T> T dispatch(Dispatcher<T> dispatcher) {
        return dispatcher.handle(this);
    }

    @Override
    public abstract String toString();
}
