package com.zarbosoft.merman.editor.serialization;

import com.zarbosoft.merman.syntax.back.BackSpec;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Deque;
import java.util.Iterator;

public class WriteStateBack extends WriteState {
    private final TSMap<String, Object> data;
    private final Iterator<BackSpec> iterator;

    public WriteStateBack(TSMap<String, Object> data, final Iterator<BackSpec> iterator) {
        this.data = data;
        this.iterator = iterator;
    }

    @Override
    public void run(final Deque<WriteState> stack, final EventConsumer writer) {
        if (!iterator.hasNext()) {
            stack.removeLast();
            return;
        }
        BackSpec part = iterator.next();
        part.write(stack, data, writer);
    }
}
