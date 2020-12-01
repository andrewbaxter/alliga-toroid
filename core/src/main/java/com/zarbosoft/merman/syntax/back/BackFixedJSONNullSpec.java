package com.zarbosoft.merman.syntax.back;

import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.editor.backevents.JNullEvent;
import com.zarbosoft.merman.editor.serialization.Write;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;

import java.util.Deque;

public class BackFixedJSONNullSpec extends BackSpec {

  @Override
  public Node buildBackRule(final Syntax syntax, final AtomType atomType) {
    return new MatchingEventTerminal(new JNullEvent());
  }

  @Override
  public void write(Deque<Write.WriteState> stack, Atom base, Write.EventConsumer writer) {
    writer.jsonNull();
  }
}
