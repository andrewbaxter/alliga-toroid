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
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Map;

public class BackKeySpec extends BackSpec {
  private final BackSpec key;
  private final BackSpec value;

  public BackKeySpec(BackSpec key, BackSpec value) {
    this.key = key;
    this.value = value;
  }

  @Override
  protected ROList<BackSpec> walkTypeBackStep() {
    return new TSList<>(key, value);
  }

  @Override
  public void finish(MultiError errors, Syntax syntax, SyntaxPath typePath) {
    super.finish(errors, syntax, typePath);
    key.finish(errors, syntax, typePath);
    BackSpec.checkSingularNotKey(errors, syntax, typePath.add("key"), key);
    value.finish(errors, syntax, typePath);
    BackSpec.checkSingularNotKey(errors, syntax, typePath.add("value"), value);
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    return new MergeSequence<AtomType.FieldParseResult>()
        .add(key.buildBackRule(env, syntax))
        .add(value.buildBackRule(env, syntax));
  }

  @Override
  public void write(
      Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    key.write(env, stack, data, writer);
    value.write(env, stack, data, writer);
  }
}
