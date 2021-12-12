package com.zarbosoft.alligatoroid.compiler.cache;

import com.zarbosoft.alligatoroid.compiler.mortar.Record;

public interface GraphSerializableMeta {
    public GraphSerializable graphDeserialize(Record data);
}
