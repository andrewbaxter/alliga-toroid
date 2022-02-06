package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.ROList;

public class ImportLoopPre extends Error.PreError {
    public final ROList<ImportId> loop;

    public ImportLoopPre(ROList<ImportId> loop) {
        this.loop = loop;
    }

    @Override
    public String toString() {
        return "This import creates an import loop.";
    }
}
