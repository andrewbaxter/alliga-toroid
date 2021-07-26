package com.zarbosoft.merman.core.serialization;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Iterator;

public class WriteStateBack extends WriteState {
    private final TSMap<String, Object> data;
    private final Iterator<BackSpec> iterator;

    public WriteStateBack(TSMap<String, Object> data, final Iterator<BackSpec> iterator) {
        this.data = data;
        this.iterator = iterator;
    }

    @Override
    public void run(Environment env, final TSList<WriteState> stack, final EventConsumer writer) {
        BackSpec next = iterator.next();
        if (iterator.hasNext()) {
            stack.add(this);
        }
        next.write(env, stack, data, writer);
    }
}
