package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.backevents.BackEvent;
import com.zarbosoft.merman.core.backevents.EPrimitiveEvent;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.Discard;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Iterator;
import java.util.Map;

public class BackFixedPrimitiveSpec extends BackSpec {

  public final String value;

  public BackFixedPrimitiveSpec(String value) {
    this.value = value;
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    return new Discard<>(new MatchingEventTerminal<BackEvent>(new EPrimitiveEvent(value)));
  }

  @Override
  public void write(Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    writer.primitive(value);
  }

  @Override
  protected ROList<BackSpec> walkTypeBackStep() {
    return ROList.empty;
  }
}
