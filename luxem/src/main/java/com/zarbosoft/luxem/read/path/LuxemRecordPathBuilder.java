package com.zarbosoft.luxem.read.path;

import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

public class LuxemRecordPathBuilder extends LuxemPathBuilder {
  private boolean type = false;
  private boolean key = false;
  private int index = -1;

  public LuxemRecordPathBuilder(final LuxemPathBuilder parent) {
    this.parent = parent;
  }

  public LuxemRecordPathBuilder(
      final LuxemPathBuilder parent, boolean key, final boolean type, final int index) {
    this.parent = parent;
    this.key = key;
    this.type = type;
    this.index = index;
  }

  @Override
  public LuxemPathBuilder unkey() {
    throw new DeadCode();
  }

  @Override
  public LuxemPathBuilder value() {
    LuxemRecordPathBuilder previous = this;
    if (previous.key) {
      if (previous.type) return new LuxemRecordPathBuilder(parent, true, false, index);
      else return new LuxemRecordPathBuilder(parent, false, false, index);
    } else {
      if (previous.type) return new LuxemRecordPathBuilder(parent, false, false, index);
      else return new LuxemRecordPathBuilder(parent, true, false, index + 1);
    }
  }

  @Override
  public LuxemPathBuilder type() {
    LuxemRecordPathBuilder previous = this;
    if (previous.key) {
      return new LuxemRecordPathBuilder(parent, false, true, index);
    } else {
      return new LuxemRecordPathBuilder(parent, true, true, index + 1);
    }
  }

  @Override
  protected void renderInternal(TSList<ROPair<Integer, Boolean>> values) {
    if (parent != null) parent.renderInternal(values);
    values.add(new ROPair<>(index, key));
  }

  @Override
  public String toString() {
    return String.format(
        "%s/%s%s",
        parent == null ? "" : parent.toString(),
        index == -1 ? "" : Integer.toString(index),
        key ? " key" : "");
  }
}
