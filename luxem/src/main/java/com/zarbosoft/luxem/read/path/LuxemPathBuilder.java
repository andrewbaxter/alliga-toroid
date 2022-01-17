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

  public LuxemPathBuilder pushArrayOpen() {
    return new LuxemArrayPathBuilder(this);
  }

  public LuxemPathBuilder pushRecordOpen() {
    return new LuxemRecordPathBuilder(this);
  }

  public abstract LuxemPathBuilder value();

  public abstract LuxemPathBuilder type();

  public LuxemPathBuilder pop() {
    return parent;
  }

  protected abstract void renderInternal(TSList<LuxemPath.Element> values);

  public LuxemPath render() {
    final TSList<LuxemPath.Element> data = new TSList<>();
    renderInternal(data);
    return new LuxemPath(data);
  }
}
