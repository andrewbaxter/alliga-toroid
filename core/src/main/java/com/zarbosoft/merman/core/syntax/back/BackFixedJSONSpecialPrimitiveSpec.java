package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.backevents.BackEvent;
import com.zarbosoft.merman.core.backevents.JSpecialPrimitiveEvent;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.BackType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.error.BackElementUnsupportedInBackFormat;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.Discard;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Iterator;
import java.util.Map;

public class BackFixedJSONSpecialPrimitiveSpec extends BackSpec {
  public final String value;

  public BackFixedJSONSpecialPrimitiveSpec(String value) {
    this.value = value;
  }

  @Override
  public void finish(MultiError errors, Syntax syntax, SyntaxPath typePath) {
    if (syntax.backType != BackType.JSON) {
      errors.add(new BackElementUnsupportedInBackFormat("json special primitive", syntax.backType, typePath));
    }
    super.finish(errors, syntax, typePath);
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    return new Discard<AtomType.FieldParseResult>(
        new MatchingEventTerminal<BackEvent>(new JSpecialPrimitiveEvent(value)));
  }

  @Override
  public void write(Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    writer.jsonSpecialPrimitive(value);
  }

  @Override
  protected ROList<BackSpec> walkTypeBackStep() {
    return ROList.empty;
  }
}
