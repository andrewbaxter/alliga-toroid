package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.serialization.WriteStateDeepDataArray;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.error.PluralInvalidAtLocation;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Map;
import java.util.function.Consumer;

public class BackSubArraySpec extends BaseBackArraySpec {
  public BackSubArraySpec(Config config) {
    super(config);
  }

  @Override
  public void copy(Context context, TSList<Atom> children) {
    context.copy(Context.CopyContext.ARRAY, new TSList<>(writeContents(children)));
  }

  @Override
  public void uncopy(Context context, Consumer<ROList<Atom>> consumer) {
    context.uncopy(
        buildBackRuleInner(context.env, context.syntax), Context.CopyContext.ARRAY, consumer);
  }

  @Override
  public Node<ROList<AtomType.FieldParseResult>> buildBackRule(Environment env, Syntax syntax) {
    return buildBackRuleInnerEnd(buildBackRuleInner(env, syntax));
  }

  @Override
  public void write(
      Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    stack.add(writeContents((TSList<Atom>) data.get(id)));
  }

  private WriteState writeContents(ROList<Atom> atoms) {
    return new WriteStateDeepDataArray(atoms, splayedBoilerplate);
  }

  @Override
  public ROPair<Atom, Integer> backLocate(Atom at, int offset, ROList<ROPair<Integer, Boolean>> segments) {
    final FieldArray data = (FieldArray) at.namedFields.get(id);
    for (Atom e : data.data) {
      final ROPair<Atom, Integer> res = e.backLocate(offset, segments);
      if (res == null) return null;
      if (res.first != null) return res;
      offset = res.second;
    }
    return new ROPair<>(null, offset);
  }

  @Override
  public void walkSingularBack(Syntax syntax, SyntaxPath path, SingularBackWalkCb cb) {
    boolean descend = cb.consume(path, this);
    if (descend) {
      for (AtomType atomType : syntax.splayedTypes.get(type)) {
        atomType.back().walkSingularBack(syntax, new SyntaxPath(atomType.id), cb);
      }
    }
  }
}
