package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class AccessNotSupported extends Error.LocationError {
    public AccessNotSupported(Location location) {
        super(location);
    }

    @Override
    public String toString() {
        return "Fields of this value cannot be accessed";
    }
}
