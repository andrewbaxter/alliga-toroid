package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.editor.backevents.EArrayCloseEvent;
import com.zarbosoft.merman.core.editor.backevents.EArrayOpenEvent;
import com.zarbosoft.merman.core.editor.serialization.EventConsumer;
import com.zarbosoft.merman.core.editor.serialization.WriteState;
import com.zarbosoft.merman.core.editor.serialization.WriteStateArrayEnd;
import com.zarbosoft.merman.core.editor.serialization.WriteStateDeepDataArray;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;
import com.zarbosoft.pidgoon.nodes.Sequence;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

public class BackArraySpec extends BaseBackSimpleArraySpec {
  public BackArraySpec(Config config) {
    super(config);
  }

  @Override
  public Node buildBackRule(final Syntax syntax) {
    return new Sequence()
        .add(new MatchingEventTerminal(new EArrayOpenEvent()))
        .visit(s -> buildBackRuleInner(syntax, s))
        .add(new MatchingEventTerminal(new EArrayCloseEvent()))
        .visit(s -> buildBackRuleInnerEnd(s));
  }

  @Override
  public void write(TSList<WriteState> stack, TSMap<String, Object> data, EventConsumer writer) {
    writer.arrayBegin();
    stack.add(new WriteStateArrayEnd());
    stack.add(new WriteStateDeepDataArray(((TSList<Atom>) data.get(id)), splayedBoilerplate));
  }

  @Override
  protected boolean isSingularValue() {
    return true;
  }
}
