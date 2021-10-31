package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.MergeSequence;
import com.zarbosoft.pidgoon.nodes.Reference;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Map;

public class BackDiscardKeySpec extends BackSpec {
  private final BackSpec key;

  public BackDiscardKeySpec(BackSpec key) {
    this.key = key;
  }

  @Override
  protected ROList<BackSpec> walkTypeBackStep() {
    return new TSList<>(key);
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    return new MergeSequence<AtomType.FieldParseResult>()
        .add(key.buildBackRule(env, syntax))
        .addIgnored(new Reference<>(Syntax.GRAMMAR_WILDCARD_KEY));
  }

  @Override
  public void write(
      Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {}

  @Override
  public void finish(MultiError errors, Syntax syntax, SyntaxPath typePath) {
    super.finish(errors, syntax, typePath);
    key.finish(errors, syntax, typePath);
    checkSingularNotKey(errors, syntax, typePath.add("key"),key);
  }
}
