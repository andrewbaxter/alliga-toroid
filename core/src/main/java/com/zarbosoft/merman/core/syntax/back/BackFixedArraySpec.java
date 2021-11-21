package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.backevents.EArrayCloseEvent;
import com.zarbosoft.merman.core.backevents.EArrayOpenEvent;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.serialization.WriteStateArrayEnd;
import com.zarbosoft.merman.core.serialization.WriteStateBack;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.MergeSequence;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Map;

public class BackFixedArraySpec extends BackSpec {
  public final ROList<BackSpec> elements;

  public BackFixedArraySpec(Config config) {
    this.elements = config.elements;
  }

  @Override
  public ROPair<Atom, Integer> backLocate(
      Atom at, int offset, ROList<ROPair<Integer, Boolean>> segments) {
    if (segments.get(0).first != offset) return new ROPair<>(null, offset + 1);
    segments = segments.subFrom(1);
    if (segments.none()) return new ROPair<>(at, null);
    offset = 0;
    for (BackSpec element : elements) {
      ROPair<Atom, Integer> res = element.backLocate(at, offset, segments);
      if (res == null || res.first != null) return res;
      offset = res.second;
    }
    return null;
  }

  @Override
  protected ROList<BackSpec> walkTypeBackStep() {
    return elements;
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    final MergeSequence<AtomType.FieldParseResult> sequence = new MergeSequence<>();
    sequence.addIgnored(new MatchingEventTerminal<>(new EArrayOpenEvent()));
    for (final BackSpec element : elements) {
      sequence.add(element.buildBackRule(env, syntax));
    }
    sequence.addIgnored(new MatchingEventTerminal<>(new EArrayCloseEvent()));
    return sequence;
  }

  @Override
  public void finish(MultiError errors, final Syntax syntax, final SyntaxPath typePath) {
    super.finish(errors, syntax, typePath);
    for (int i = 0; i < elements.size(); ++i) {
      BackSpec element = elements.get(i);
      element.finish(errors, syntax, typePath.add(Integer.toString(i)));
      checkNotKey(errors, syntax, typePath.add(Integer.toString(i)), element);
      int finalI = i;
      element.parent =
          new PartParent() {
            @Override
            public BackSpec part() {
              return BackFixedArraySpec.this;
            }

            @Override
            public String pathSection() {
              return Integer.toString(finalI);
            }
          };
    }
  }

  @Override
  public void write(
      Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    writer.arrayBegin();
    stack.add(new WriteStateArrayEnd());
    stack.add(new WriteStateBack(data, elements.iterator()));
  }

  public static class Config {
    public ROList<BackSpec> elements;

    public Config elements(ROList<BackSpec> elements) {
      this.elements = elements;
      return this;
    }
  }
}
