package com.zarbosoft.merman.core.serialization;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.syntax.back.BackSpec;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Iterator;
import java.util.Map;

public class WriteStateFixedRecord extends WriteState {
  private final Map<Object, Object> data;
  private final Iterator<BackSpec> iterator;

  public WriteStateFixedRecord(final Map<Object, Object> data, final ROList<BackSpec> record) {
    this.data = data;
    this.iterator = record.iterator();
  }

  @Override
  public void run(Environment env, final TSList<WriteState> stack, final EventConsumer writer) {
    if (!iterator.hasNext()) {
        return;
    }
    final BackSpec next = iterator.next();
    if (iterator.hasNext()) {
      stack.add(this);
    }
    next.write(env, stack, data, writer);
  }
}
