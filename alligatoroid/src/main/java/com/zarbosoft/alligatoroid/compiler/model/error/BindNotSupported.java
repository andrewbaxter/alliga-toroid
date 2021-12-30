package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class BindNotSupported extends Error.LocationError {
    public BindNotSupported(Location location) {
        super(location);
    }

    @Override
    public String toString() {
        return "This value cannot be bound to a variable";
    }
}
