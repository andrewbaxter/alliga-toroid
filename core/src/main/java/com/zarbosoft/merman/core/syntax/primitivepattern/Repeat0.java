package com.zarbosoft.merman.core.syntax.primitivepattern;

import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.Repeat;

public class Repeat0 extends Pattern {
  public final Pattern pattern;

  public Repeat0(Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  public Node build() {
    return new Repeat(pattern.build());
  }
}
