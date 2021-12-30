package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Format;

public class DeserializeNotBool extends Error.DeserializeError {
    public final String value;

    public DeserializeNotBool(LuxemPath path, String value) {
        super(path);
        this.value = value;
    }

    @Override
    public String toString() {
        return Format.format("Expected a bool (true/false) in luxem but got [%s]", value);
    }
}
