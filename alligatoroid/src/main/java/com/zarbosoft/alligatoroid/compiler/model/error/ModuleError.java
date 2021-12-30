package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class ModuleError extends Error.LocationError {
    public ModuleError(Location location) {
        super(location);
    }

    @Override
    public String toString() {
        return Format.format("There were errors compiling this module.");
    }
}
