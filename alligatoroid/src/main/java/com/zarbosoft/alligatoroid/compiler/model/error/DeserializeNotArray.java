package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;

public class DeserializeNotArray extends Error.DeserializeError {
    public DeserializeNotArray(LuxemPath path) {
        super(path);
    }

    @Override
    public String toString() {
        return "A luxem array is not allowed at this location in the source";
    }
}
