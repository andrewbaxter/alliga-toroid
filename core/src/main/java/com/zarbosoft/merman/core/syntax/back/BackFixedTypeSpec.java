package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.BackPath;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.backevents.BackEvent;
import com.zarbosoft.merman.core.backevents.ETypeEvent;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.serialization.WriteStateBack;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.BackType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.error.BackElementUnsupportedInBackFormat;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.MergeSequence;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Arrays;
import java.util.Map;

public class BackFixedTypeSpec extends BackSpec {
  public final String type;
  public final BackSpec value;

  public BackFixedTypeSpec(Config config) {
    this.type = config.type;
    this.value = config.value;
  }

  @Override
  public ROPair<Atom, Integer> backLocate(Atom at, int offset, ROList<BackPath.Element> segments) {
    return value.backLocate(at, offset, segments);
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    return new MergeSequence<AtomType.FieldParseResult>()
        .addIgnored(new MatchingEventTerminal<BackEvent>(new ETypeEvent(type)))
        .add(value.buildBackRule(env, syntax));
  }

  @Override
  public void finish(MultiError errors, final Syntax syntax, final SyntaxPath typePath) {
    super.finish(errors, syntax, typePath);
    if (syntax.backType != BackType.LUXEM) {
      errors.add(new BackElementUnsupportedInBackFormat("type", syntax.backType, typePath));
    }
    value.finish(errors, syntax, typePath.add("value"));
    value.parent =
        new PartParent() {
          @Override
          public BackSpec part() {
            return BackFixedTypeSpec.this;
          }

          @Override
          public String pathSection() {
            return null;
          }
        };
    checkSingularNotKey(errors, syntax, typePath.add("value"), value);
  }

  @Override
  public void write(
      Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    writer.type(type);
    stack.add(new WriteStateBack(data, Arrays.asList(value).iterator()));
  }

  @Override
  protected ROList<BackSpec> walkTypeBackStep() {
    return TSList.of(value);
  }

  public static class Config {
    public final String type;
    public final BackSpec value;

    public Config(String type, BackSpec value) {
      this.type = type;
      this.value = value;
    }
  }
}
