package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;

import java.util.concurrent.ExecutionException;

public class CacheUnexpectedPre extends Error.PreError {
    public final String cachePath;
    public final Throwable exception;

    public CacheUnexpectedPre(String cachePath, Throwable exception) {
        if (exception instanceof Error.PreError || exception instanceof ExecutionException) throw new Assertion();
        this.cachePath = cachePath;
        this.exception = exception;
    }

    @Override
    public Error toError(Location location) {
        return new CacheUnexpected(location, cachePath, exception);
    }
}
