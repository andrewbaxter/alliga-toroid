package com.zarbosoft.merman.core;

import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class BackPath {
  /** Offset, true=key,false=value (false except in records) */
  public final ROList<ROPair<Integer, Boolean>> segments;

  public BackPath(final ROList<ROPair<Integer, Boolean>> segments) {
    this.segments = segments;
  }

  public ROList<ROPair<Integer, Boolean>> toList() {
    return segments;
  }
}
