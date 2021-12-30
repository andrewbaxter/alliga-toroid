package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class DeserializeIncompleteFile extends CacheFileError {
    public DeserializeIncompleteFile(Location location, String cachePath) {
        super(location, cachePath);
    }

    @Override
    public String toString() {
        return "This source file ended before all expected data was read";
    }
}
