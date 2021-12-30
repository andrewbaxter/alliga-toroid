package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.rendaw.common.Format;

public class ImportOutsideOwningBundleModule extends Error.LocationError {
    public final String subpath;
    public final ModuleId module;

    public ImportOutsideOwningBundleModule(Location location, String subpath, ModuleId module) {
        super(location);
        this.subpath = subpath;
        this.module = module;
    }

    @Override
    public String toString() {
        return Format.format(
                "Local import of %s within remote submodule %s goes outside the module", subpath, module);
    }
}
