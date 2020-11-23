package com.zarbosoft.merman.syntax.back;

import com.zarbosoft.merman.editor.backevents.EPrimitiveEvent;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;

public class BackFixedPrimitiveSpec extends BackSpec {

  public String value;

  @Override
  public Node buildBackRule(final Syntax syntax, final AtomType atomType) {
    return new MatchingEventTerminal(new EPrimitiveEvent(value));
  }
}
