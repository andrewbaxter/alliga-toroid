package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;

public class ImportNotFoundPre extends Error.PreError {
    public final String path;

    public ImportNotFoundPre(String path) {
        this.path = path;
    }

    @Override
    public Error toError(Location location) {
        return new ImportNotFound(location, path);
    }
}
