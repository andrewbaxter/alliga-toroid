package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

public class DeserializeUnknownType extends Error.DeserializeError {
    public final String type;
    public final ROList<String> knownTypes;

    public DeserializeUnknownType(LuxemPath path, String type, ROList<String> knownTypes) {
        super(path);
        this.type = type;
        this.knownTypes = knownTypes;
    }

    @Override
    public String toString() {
        return Format.format("Unknown luxem type [%s]", type);
    }
}
