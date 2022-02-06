package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Assertion;

public class AccessNotSupported extends Error.LocationError {
    public AccessNotSupported(Location location) {
        super(location, new PreError() {
            @Override
            public String toString() {
                return "Fields of this value cannot be accessed";
            }
        });
    }
}
