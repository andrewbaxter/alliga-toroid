package com.zarbosoft.alligatoroid.compiler;

import com.zarbosoft.alligatoroid.compiler.cache.GraphSerializableMeta;

public interface BuiltinMeta extends GraphSerializableMeta {
    /**
     * The class this meta represents
     * @return
     */
    Class getKlass();
}
