package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;

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
    public String toString() {
        return Format.format("An unexpected error occurred while loading cache file: %s", exception);
    }
}
