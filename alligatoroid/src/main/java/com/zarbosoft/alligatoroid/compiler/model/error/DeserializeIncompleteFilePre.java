package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class DeserializeIncompleteFilePre extends Error.PreError {
    public final String cachePath;

    public DeserializeIncompleteFilePre(String cachePath) {
        this.cachePath = cachePath;
    }

    @Override
    public Error toError(Location location) {
        return new DeserializeIncompleteFile(location, cachePath);
    }
}
