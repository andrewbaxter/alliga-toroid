package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.backevents.BackEvent;
import com.zarbosoft.merman.core.backevents.EPrimitiveEvent;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.pidgoon.events.nodes.Terminal;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Iterator;

public class BackPrimitiveSpec extends BaseBackPrimitiveSpec {
  public BackPrimitiveSpec(Config config) {
    super(config);
  }

  @Override
  protected Iterator<BackSpec> walkStep() {
    return null;
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    return new Terminal<BackEvent, ROList<AtomType.FieldParseResult>>() {
      @Override
      protected ROPair<Boolean, ROList<AtomType.FieldParseResult>> matches(BackEvent event) {
        if (!(event instanceof EPrimitiveEvent)) return new ROPair<>(false, null);
        boolean ok = matcher == null || matcher.match(env, ((EPrimitiveEvent) event).value);
        return new ROPair<>(
            ok,
            ok
                ? TSList.of(new AtomType.PrimitiveFieldParseResult(
                    id, new FieldPrimitive(BackPrimitiveSpec.this, ((EPrimitiveEvent) event).value)))
                : ROList.empty);
      }

      @Override
      public String toString() {
        return matcher == null ? "ANY PRIMITIVE" : ("PRIMITIVE - " + patternDescription);
      }
    };
  }

  @Override
  public void write(TSList<WriteState> stack, TSMap<String, Object> data, EventConsumer writer) {
    writer.primitive(((StringBuilder) data.get(id)).toString());
  }

  @Override
  protected boolean isSingularValue() {
    return true;
  }

  @Override
  protected boolean isTypedValue() {
    return false;
  }
}
