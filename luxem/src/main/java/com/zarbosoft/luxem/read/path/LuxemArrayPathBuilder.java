package com.zarbosoft.luxem.read.path;

import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

public class LuxemArrayPathBuilder extends LuxemPathBuilder {
  private boolean type = false;
  private int index = -1;

  public LuxemArrayPathBuilder(final LuxemPathBuilder parent) {
    this.parent = parent;
  }

  public LuxemArrayPathBuilder(final LuxemPathBuilder parent, final boolean type, final int index) {
    this.parent = parent;
    this.type = type;
    this.index = index;
  }

  @Override
  public LuxemPathBuilder unkey() {
    throw new DeadCode();
  }

  @Override
  public LuxemPathBuilder value() {
    if (this.type) return new LuxemArrayPathBuilder(parent, false, index);
    else return new LuxemArrayPathBuilder(parent, false, index + 1);
  }

  @Override
  public LuxemPathBuilder type() {
    return new LuxemArrayPathBuilder(parent, true, index + 1);
  }

  @Override
  protected void renderInternal(TSList<ROPair<Integer, Boolean>> values) {
    if (parent != null) parent.renderInternal(values);
    values.add(new ROPair<>(index, false));
  }

  @Override
  public String toString() {
    return String.format(
        "%s/%s",
        parent == null ? "" : parent.toString(), index == -1 ? "" : ((Integer) index).toString());
  }
}
