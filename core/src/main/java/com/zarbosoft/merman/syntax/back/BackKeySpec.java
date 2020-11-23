package com.zarbosoft.merman.syntax.back;

import com.zarbosoft.merman.document.values.ValuePrimitive;
import com.zarbosoft.merman.editor.backevents.EKeyEvent;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.merman.syntax.middle.MiddlePrimitiveSpec;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.events.nodes.ClassEqTerminal;
import com.zarbosoft.pidgoon.events.stores.StackStore;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.rendaw.common.Pair;

import java.util.Set;

public class BackKeySpec extends BackSpec {

  public String middle;

  @Override
  public Node buildBackRule(final Syntax syntax, final AtomType atomType) {
    final MiddlePrimitiveSpec middle = atomType.getDataPrimitive(this.middle);
    return new Operator<StackStore>(new ClassEqTerminal(EKeyEvent.class)) {
      @Override
      protected StackStore process(StackStore store) {
        return store.stackSingleElement(
            new Pair<>(
                BackKeySpec.this.middle,
                new ValuePrimitive(middle, ((EKeyEvent) store.top()).value)));
      }
    };
  }

  public void finish(final Syntax syntax, final AtomType atomType, final Set<String> middleUsed) {
    middleUsed.add(middle);
    atomType.getDataPrimitive(middle);
  }
}
