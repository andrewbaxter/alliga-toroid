package com.zarbosoft.merman.syntax.back;

import com.zarbosoft.merman.document.values.ValuePrimitive;
import com.zarbosoft.merman.editor.I18nEngine;
import com.zarbosoft.merman.editor.backevents.EPrimitiveEvent;
import com.zarbosoft.merman.editor.serialization.EventConsumer;
import com.zarbosoft.merman.editor.serialization.WriteState;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.events.nodes.ClassEqTerminal;
import com.zarbosoft.pidgoon.events.stores.StackStore;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;

import java.util.Iterator;

public class BackPrimitiveSpec extends BaseBackPrimitiveSpec {

  public BackPrimitiveSpec(I18nEngine i18n,Config config) {
    super(i18n,config);
  }

  @Override
  protected Iterator<BackSpec> walkStep() {
    return null;
  }

  @Override
  public Node buildBackRule(final Syntax syntax) {
    return new Operator<StackStore>(new ClassEqTerminal(EPrimitiveEvent.class.getName())) {
      @Override
      protected StackStore process(StackStore store) {
        return store.stackVarDoubleElement(
            id, new ValuePrimitive(BackPrimitiveSpec.this, ((EPrimitiveEvent) store.top()).value));
      }
    };
  }

  @Override
  public void write(
          TSList<WriteState> stack, TSMap<String, Object> data, EventConsumer writer) {
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
