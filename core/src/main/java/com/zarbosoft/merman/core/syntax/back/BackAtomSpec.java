package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.FieldAtom;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Iterator;
import java.util.Map;

public class BackAtomSpec extends BaseBackAtomSpec {
  public BackAtomSpec(Config config) {
    super(config);
  }

  @Override
  protected Iterator<BackSpec> walkStep() {
    return null;
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    return new Operator<AtomType.AtomParseResult, ROList<AtomType.FieldParseResult>>(
        syntax.backRuleRef(type)) {
      @Override
      protected ROList<AtomType.FieldParseResult> process(AtomType.AtomParseResult value) {
        return TSList.of(
            new AtomType.AtomFieldParseResult(id, new FieldAtom(BackAtomSpec.this), value));
      }
    };
  }

  @Override
  public void write(Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    ((Atom) data.get(id)).write(stack);
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
