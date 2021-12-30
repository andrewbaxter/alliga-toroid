package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.rendaw.common.Format;

public class UnknownImportFileType extends Error.LocationError {
    public final ModuleId id;

    public UnknownImportFileType(Location location, ModuleId id) {
        super(location);
        this.id = id;
    }

    @Override
    public String toString() {
        return Format.format("The file type of the module [%s} is not recognized", id);
    }
}
