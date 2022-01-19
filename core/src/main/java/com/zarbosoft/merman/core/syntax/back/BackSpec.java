package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.BackPath;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.serialization.EventConsumer;
import com.zarbosoft.merman.core.serialization.WriteState;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.error.KeyInvalidAtLocation;
import com.zarbosoft.merman.core.syntax.error.NonKeyInvalidAtLocation;
import com.zarbosoft.merman.core.syntax.error.PluralInvalidAtLocation;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public abstract class BackSpec {
  public Parent parent = null;

  public static void checkNotKey(MultiError errors, Syntax syntax, String rootType) {
    if (rootType == null) return;
    for (AtomType atomType : syntax.splayedTypes.get(rootType)) {
      checkNotKey(errors, syntax, new SyntaxPath(atomType.id), atomType.back());
    }
  }

  public static void checkNotKey(
      MultiError errors, Syntax syntax, SyntaxPath rootPath, BackSpec root) {
    root.walkSingularBack(
        syntax,
        rootPath,
        new SingularBackWalkCb() {
          @Override
          public boolean consume(SyntaxPath path, BackSpec backSpec) {
            if (backSpec instanceof BackKeySpec || backSpec instanceof BackDiscardKeySpec) {
              errors.add(new KeyInvalidAtLocation(rootPath, path));
            }
            return true;
          }
        });
  }

  public static void checkKey(MultiError errors, Syntax syntax, String rootType) {
    for (AtomType atomType : syntax.splayedTypes.get(rootType)) {
      checkKey(errors, syntax, new SyntaxPath(atomType.id), atomType.back());
    }
  }

  public static void checkKey(
      MultiError errors, Syntax syntax, SyntaxPath rootPath, BackSpec root) {
    root.walkSingularBack(
        syntax,
        rootPath,
        new SingularBackWalkCb() {
          @Override
          public boolean consume(SyntaxPath path, BackSpec backSpec) {
            if (!(backSpec instanceof BackKeySpec)) {
              errors.add(new NonKeyInvalidAtLocation(rootPath, path));
            }
            return true;
          }
        });
  }

  private static boolean isSingular(BackSpec backSpec) {
    return backSpec instanceof BackSubArraySpec || backSpec instanceof BackFixedSubArraySpec;
  }

  private static boolean isKey(BackSpec backSpec) {
    return backSpec instanceof BackKeySpec || backSpec instanceof BackDiscardKeySpec;
  }

  public static void checkSingularNotKey(
      MultiError errors, Syntax syntax, SyntaxPath rootPath, BackSpec root) {
    root.walkSingularBack(
        syntax,
        rootPath,
        new SingularBackWalkCb() {
          @Override
          public boolean consume(SyntaxPath path, BackSpec backSpec) {
            if (isKey(backSpec)) {
              errors.add(new KeyInvalidAtLocation(rootPath, path));
            } else if (isSingular(backSpec)) {
              errors.add(new PluralInvalidAtLocation(rootPath, path));
            }
            return false;
          }
        });
  }

  public static void walkTypeBack(BackSpec root, Function<BackSpec, Boolean> consumer) {
    TSList<Iterator<BackSpec>> stack = new TSList<>();
    stack.add(Arrays.asList(root).iterator());
    while (!stack.isEmpty()) {
      Iterator<BackSpec> top = stack.last();
      BackSpec next = top.next();
      if (!top.hasNext()) {
        stack.removeLast();
      }
      boolean cont = consumer.apply(next);
      if (cont) {
        ROList<BackSpec> children = next.walkTypeBackStep();
        if (children.some()) {
          stack.add(children.iterator());
        }
      }
    }
  }

  public void walkSingularBack(Syntax syntax, SyntaxPath path, SingularBackWalkCb cb) {
    cb.consume(path, this);
  }

  protected ROList<BackSpec> walkTypeBackStep() {
    return new TSList<>(this);
  }

  public abstract Node<ROList<AtomType.FieldParseResult>> buildBackRule(
      Environment env, Syntax syntax);

  public void finish(MultiError errors, final Syntax syntax, final SyntaxPath typePath) {}

  /**
   * @param env
   * @param stack
   * @param data map of ids (named fields) or back specs (unnamed fields) to data (StringBuilder,
   *     Atom, List of Atom, int, etc)
   * @param writer
   */
  public abstract void write(
      Environment env, TSList<WriteState> stack, Map<Object, Object> data, EventConsumer writer);

  /**
   * Return null if path doesn't exist, a pair with a null atom + new offset if path outside of this
   * element, or an atom and null int if path was located
   *
   * @param at
   * @param offset
   * @param segments
   * @return
   */
  public ROPair<Atom, Integer> backLocate(Atom at, int offset, ROList<BackPath.Element> segments) {
    BackPath.Element target = segments.get(0);
    if (target.index == offset) {
      if (segments.size() > 1) return null;
      else return new ROPair<>(at, null);
    } else return new ROPair<>(null, offset + 1);
  }

  @FunctionalInterface
  public interface SingularBackWalkCb {
    public boolean consume(SyntaxPath path, BackSpec spec);
  }

  public abstract static class Parent {}

  public abstract static class PartParent extends Parent {
    public abstract BackSpec part();

    public abstract String pathSection();
  }
}
