package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.TSList;

public class ImportLoopPre extends Error.PreError {
    public final TSList<ImportId> loop;

    public ImportLoopPre(TSList<ImportId> loop) {
        this.loop = loop;
    }

    @Override
    public Error toError(Location location) {
        return new ImportLoop(location, loop);
    }
}
