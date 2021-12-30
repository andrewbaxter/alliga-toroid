package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

public class Unexportable extends Error.LocationError {
    private final ROList<String> accessPath;

    public Unexportable(Location location, ROList<String> accessPath) {
        super(location);
        this.accessPath = accessPath;
    }

    @Override
    public String toString() {
        return Format.format("Object at [%s] cannot be exported", accessPath);
    }
}
