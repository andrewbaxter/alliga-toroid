package com.zarbosoft.merman.core.syntax.primitivepattern;

import com.zarbosoft.pidgoon.events.EscapableResult;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.Union;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;

public class PatternCharacterClass extends Pattern {
  private final ROList<ROPair<String, String>> ranges;

  public PatternCharacterClass(ROList<ROPair<String, String>> ranges) {
    this.ranges = ranges;
  }

  @Override
  public Node<EscapableResult<ROList<String>>> build(boolean capture) {
    Union<EscapableResult<ROList<String>>> union = new Union<>();
    for (ROPair<String, String> range : ranges) {
      union.add(new CharacterRangeTerminal(capture, range.first, range.second));
    }
    return union;
  }
}
