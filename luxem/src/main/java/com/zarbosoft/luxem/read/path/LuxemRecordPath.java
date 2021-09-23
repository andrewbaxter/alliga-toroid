package com.zarbosoft.luxem.read.path;

import com.zarbosoft.rendaw.common.DeadCode;

public class LuxemRecordPath extends LuxemPath {
  private boolean type = false;
  private boolean key = false;
  private int index = -1;

  public LuxemRecordPath(final LuxemPath parent) {
    this.parent = parent;
  }

  public LuxemRecordPath(final LuxemPath parent, boolean key, final boolean type, final int index) {
    this.parent = parent;
    this.key = key;
    this.type = type;
    this.index = index;
  }

  @Override
  public LuxemPath unkey() {
    throw new DeadCode();
  }

  @Override
  public LuxemPath value() {
    LuxemRecordPath previous = this;
    if (previous.key) {
      if (previous.type) return new LuxemRecordPath(parent, true, false, index);
      else return new LuxemRecordPath(parent, false, false, index);
    } else {
      if (previous.type) return new LuxemRecordPath(parent, false, false, index);
      else return new LuxemRecordPath(parent, true, false, index + 1);
    }
  }

  @Override
  public LuxemPath type() {
    LuxemRecordPath previous = this;
    if (previous.key) {
      return new LuxemRecordPath(parent, false, true, index);
    } else {
      return new LuxemRecordPath(parent, true, true, index + 1);
    }
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
