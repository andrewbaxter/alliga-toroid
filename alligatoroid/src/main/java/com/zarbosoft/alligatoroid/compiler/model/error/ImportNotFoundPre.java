package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.rendaw.common.Format;

public class ImportNotFoundPre extends Error.PreError {
    public final String url;

    public ImportNotFoundPre(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return Format.format("The import [%s] could not be found", url);
    }
}
