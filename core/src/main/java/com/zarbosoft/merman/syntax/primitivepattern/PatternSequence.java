package com.zarbosoft.merman.syntax.primitivepattern;

import com.zarbosoft.merman.misc.ROList;
import com.zarbosoft.pidgoon.Node;

public class PatternSequence extends Pattern {
  public final ROList<Pattern> children;

  public PatternSequence(ROList<Pattern> children) {
    this.children = children;
  }

  @Override
  public Node build() {
    final com.zarbosoft.pidgoon.nodes.Sequence out = new com.zarbosoft.pidgoon.nodes.Sequence();
    for (final Pattern child : children) out.add(child.build());
    return out;
  }
}
