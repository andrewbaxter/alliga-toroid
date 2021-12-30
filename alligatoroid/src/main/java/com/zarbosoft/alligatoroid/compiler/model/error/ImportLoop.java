package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;

public class ImportLoop extends Error.LocationError {
    public final ROList<ImportId> loop;

    public ImportLoop(Location location, ROList<ImportId> loop) {
        super(location);
        this.loop = loop;
    }

    @Override
    public String toString() {
        return "This import creates an import loop.";
    }
}
