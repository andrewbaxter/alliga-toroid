package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.BackPath;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.Field;
import com.zarbosoft.merman.core.document.fields.FieldAtom;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.error.AtomTypeDoesntExist;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROOrderedSetRef;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Map;

public class BackAtomSpec extends BackSpecData {
  /** Type/group name or null; null means any type */
  public final String type;

  public BackAtomSpec(Config config) {
    super(config.id);
    this.type = config.type;
  }

  @Override
  protected ROList<BackSpec> walkTypeBackStep() {
    return ROList.empty;
  }

  @Override
  public ROPair<Atom, Integer> backLocate(Atom at, int offset, ROList<BackPath.Element> segments) {
    FieldAtom data = (FieldAtom) at.namedFields.get(id);
    return data.data.backLocate(offset, segments);
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
  public void write(
      Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer) {
    ((Atom) data.get(id)).write(stack);
  }

  public FieldAtom get(final ROMap<String, Field> data) {
    return (FieldAtom) data.getOpt(id);
  }

  @Override
  public void finish(MultiError errors, Syntax syntax, SyntaxPath typePath) {
    super.finish(errors, syntax, typePath);
    if (type == null) return; // Gaps have null type, take anything
    ROOrderedSetRef<AtomType> childTypes = syntax.splayedTypes.getOpt(type);
    if (childTypes == null) {
      errors.add(new AtomTypeDoesntExist(typePath, type));
    } else {
      for (AtomType childType : childTypes) {
        BackSpec.checkSingularNotKey(errors, syntax, typePath, childType.back());
      }
    }
  }

  public static class Config {
    public final String type;
    public final String id;

    public Config(String id, String type) {
      this.type = type;
      this.id = id;
    }
  }
}
