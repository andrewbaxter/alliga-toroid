package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;

public class UnknownImportFileTypePre extends Error.PreError {
    public final ModuleId id;

    public UnknownImportFileTypePre(ModuleId id) {
        this.id = id;
    }

    @Override
    public Error toError(Location location) {
        return new UnknownImportFileType(location, id);
    }
}
