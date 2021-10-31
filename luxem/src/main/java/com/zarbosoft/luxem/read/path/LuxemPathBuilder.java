package com.zarbosoft.luxem.read.path;

import com.zarbosoft.luxem.events.LArrayCloseEvent;
import com.zarbosoft.luxem.events.LArrayOpenEvent;
import com.zarbosoft.luxem.events.LPrimitiveEvent;
import com.zarbosoft.luxem.events.LRecordCloseEvent;
import com.zarbosoft.luxem.events.LRecordOpenEvent;
import com.zarbosoft.luxem.events.LTypeEvent;
import com.zarbosoft.luxem.events.LuxemEvent;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

public abstract class LuxemPathBuilder {
  public LuxemPathBuilder parent;

  public abstract LuxemPathBuilder unkey();

  public LuxemPathBuilder push(final LuxemEvent e) {
    if (e.getClass() == LArrayOpenEvent.class) {
      return new LuxemArrayPathBuilder(value());
    } else if (e.getClass() == LArrayCloseEvent.class) {
      return pop();
    } else if (e.getClass() == LRecordOpenEvent.class) {
      return new LuxemRecordPathBuilder(value());
    } else if (e.getClass() == LRecordCloseEvent.class) {
      return pop();
    } else if (e.getClass() == LTypeEvent.class) {
      return type();
    } else if (e.getClass() == LPrimitiveEvent.class) {
      return value();
    } else throw new AssertionError(String.format("Unknown luxem event type [%s]", e.getClass()));
  }

  public LuxemPathBuilder pushArrayOpen() {
    return new LuxemArrayPathBuilder(value());
  }

  public LuxemPathBuilder pushRecordOpen() {
    return new LuxemRecordPathBuilder(value());
  }

  public abstract LuxemPathBuilder value();

  public abstract LuxemPathBuilder type();

  public LuxemPathBuilder pop() {
    return parent;
  }

  protected abstract void renderInternal(TSList<ROPair<Integer, Boolean>> values);

  public LuxemPath render() {
    final TSList<ROPair<Integer, Boolean>> data = new TSList<>();
    renderInternal(data);
    return new LuxemPath(data);
  }
}
