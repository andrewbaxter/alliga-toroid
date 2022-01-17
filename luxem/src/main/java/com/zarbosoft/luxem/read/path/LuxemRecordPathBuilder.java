package com.zarbosoft.luxem.read.path;

import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.TSList;

public class LuxemRecordPathBuilder extends LuxemPathBuilder {
  private final boolean key;
  private final int index;
  private final int typeCount;

  public LuxemRecordPathBuilder(final LuxemPathBuilder parent) {
    this.parent = parent;
    index = 0;
    key = true;
    typeCount = 0;
  }

  public LuxemRecordPathBuilder(
      final LuxemPathBuilder parent, int index, boolean key, int typeCount) {
    this.parent = parent;
    this.index = index;
    this.key = key;
    this.typeCount = typeCount;
  }

  @Override
  public LuxemPathBuilder unkey() {
    throw new DeadCode();
  }

  @Override
  public LuxemPathBuilder value() {
    if (key) {
      return new LuxemRecordPathBuilder(parent, index, false, 0);
    } else {
      return new LuxemRecordPathBuilder(parent, index + 1, true, 0);
    }
  }

  @Override
  public LuxemPathBuilder type() {
    return new LuxemRecordPathBuilder(parent, index, key, typeCount + 1);
  }

  @Override
  protected void renderInternal(TSList<LuxemPath.Element> values) {
    if (parent != null) parent.renderInternal(values);
    values.add(new LuxemPath.Element(index, key, typeCount));
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    if (parent != null) out.append(parent.toString());
    out.append("/");
    out.append(index);
    if (key) {
      out.append(" key");
    }
    for (int i = 0; i < typeCount; i += 1) {
      out.append(" value");
    }
    return out.toString();
  }
}
