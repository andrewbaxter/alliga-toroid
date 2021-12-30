package com.zarbosoft.alligatoroid.compiler.model.error;

import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;

public class DeserializeUnknownField extends Error.DeserializeError {
    public final String type;
    public final String field;
    public final ROList<String> fields;

    public DeserializeUnknownField(
            LuxemPath path, String type, String field, ROList<String> fields) {
        super(path);
        this.type = type;
        this.field = field;
        this.fields = fields;
    }

    @Override
    public String toString() {
        return Format.format("Luxem type [%s] does not have a field named [%s]", type, field);
    }
}
