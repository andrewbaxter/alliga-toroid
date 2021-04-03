package com.zarbosoft.merman.core.serialization;

import com.zarbosoft.rendaw.common.TSList;

public class WriteStateArrayEnd extends WriteState {
    @Override
    public void run(final TSList<WriteState> stack, final EventConsumer writer) {
        writer.arrayEnd();
    }
}
