package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Assertion;

public class DeserializeNotArray extends Error.DeserializeError {
    public DeserializeNotArray(LuxemPath path) {
        super(path, new PreError() {
            @Override
            public String toString() {
                return "A luxem array is not allowed at this location in the source";
            }
        });
    }
}
