package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Format;

public class DeserializeUnknownLanguageVersion extends Error.DeserializeError {
    public final String version;

    public DeserializeUnknownLanguageVersion(LuxemPath path, String version) {
        super(path);
        this.version = version;
    }

    @Override
    public String toString() {
        return Format.format("Language version (luxem root type) %s is unknown", version);
    }
}
