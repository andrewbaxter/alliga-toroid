package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class ImportNotFound extends Error.LocationError {
    public final String url;

    public ImportNotFound(Location location, String url) {
        super(location);
        this.url = url;
    }

    @Override
    public String toString() {
        return Format.format("The import [%s] could not be found", url);
    }
}
