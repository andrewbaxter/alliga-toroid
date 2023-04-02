package com.zarbosoft.luxem.read.path;

import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

public class LuxemArrayPathBuilder extends LuxemPathBuilder {
  private final int index;
  private final int typeCount;

  public LuxemArrayPathBuilder(final LuxemPathBuilder parent) {
    this.parent = parent;
    this.index = 0;
    this.typeCount = 0;
  }

  public LuxemArrayPathBuilder(final LuxemPathBuilder parent, final int index, int typeCount) {
    this.parent = parent;
    this.index = index;
    this.typeCount = typeCount;
  }

  @Override
  public LuxemPathBuilder unkey() {
    throw new DeadCode();
  }

  @Override
  public LuxemPathBuilder value() {
    return new LuxemArrayPathBuilder(parent, index + 1, 0);
  }

  @Override
  public LuxemPathBuilder type() {
    return new LuxemArrayPathBuilder(parent, index, typeCount + 1);
  }

  @Override
  protected void renderInternal(TSList<LuxemPath.Element> values) {
    if (parent != null) {
        parent.renderInternal(values);
    }
    values.add(new LuxemPath.Element(index, false, typeCount));
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    if (parent != null) {
        out.append(parent.toString());
    }
    out.append("/");
    out.append(index);
    for (int i = 0; i < typeCount; i += 1) {
      out.append(" value");
    }
    return out.toString();
  }
}
