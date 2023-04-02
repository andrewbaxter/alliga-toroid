package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.BackPath;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.backevents.BackEvent;
import com.zarbosoft.merman.core.backevents.EObjectCloseEvent;
import com.zarbosoft.merman.core.backevents.EObjectOpenEvent;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.serialization.WriteStateFixedRecord;
import com.zarbosoft.merman.core.serialization.WriteStateRecordEnd;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.pidgoon.events.nodes.MatchingEventTerminal;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.MergeSequence;
import com.zarbosoft.pidgoon.nodes.MergeSet;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Map;

public class BackFixedRecordSpec extends BackSpec {

  public final ROList<BackSpec> pairs;

  public BackFixedRecordSpec(Config config) {
    this.pairs = config.pairs;
  }

  @Override
  public ROPair<Atom, Integer> backLocate(Atom at, int offset, ROList<BackPath.Element> segments) {
    if (segments.get(0).index != offset) {
        return new ROPair<>(null, offset + 1);
    }
    segments = segments.subFrom(1);
    if (segments.none()) {
        return new ROPair<>(at, null);
    }
    offset = 0;
    for (BackSpec element : pairs) {
      ROPair<Atom, Integer> res = element.backLocate(at, offset, segments);
      if (res == null || res.first != null) {
          return res;
      }
      offset = res.second;
    }
    return null;
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    final MergeSequence<AtomType.FieldParseResult> sequence = new MergeSequence<>();
    sequence.addIgnored(new MatchingEventTerminal<BackEvent>(new EObjectOpenEvent()));
    final MergeSet<AtomType.FieldParseResult> set = new MergeSet<>();
    for (BackSpec pair : pairs) {
      set.add(pair.buildBackRule(env, syntax));
    }
    sequence.add(set);
    sequence.addIgnored(new MatchingEventTerminal<BackEvent>(new EObjectCloseEvent()));
    return sequence;
  }

  @Override
  public void finish(MultiError errors, final Syntax syntax, final SyntaxPath typePath) {
    super.finish(errors, syntax, typePath);
    for (int i = 0; i < pairs.size(); i++) {
      final BackSpec e = pairs.get(i);
      final SyntaxPath ePath = typePath.add(Integer.toString(i));
      checkKey(errors, syntax, ePath, e);
      e.finish(errors, syntax, ePath);
    }
  }

  @Override
  public void write(
      Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    writer.recordBegin();
    stack.add(new WriteStateRecordEnd());
    stack.add(new WriteStateFixedRecord(data, pairs));
  }

  @Override
  protected ROList<BackSpec> walkTypeBackStep() {
    return pairs;
  }

  public static class Config {
    public final ROList<BackSpec> pairs;

    public Config(ROList<BackSpec> pairs) {
      this.pairs = pairs;
    }
  }
}
