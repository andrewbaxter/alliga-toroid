package com.zarbosoft.merman.core.syntax.back;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.MultiError;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.fields.Field;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.serialization.WriteStateDeepDataArray;
import com.zarbosoft.merman.core.syntax.AtomType;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.error.ArrayBoilerplateAtomIdNotNull;
import com.zarbosoft.merman.core.syntax.error.ArrayBoilerplateDuplicateType;
import com.zarbosoft.merman.core.syntax.error.ArrayBoilerplateMissingAtom;
import com.zarbosoft.merman.core.syntax.error.ArrayBoilerplateNotInBaseSet;
import com.zarbosoft.merman.core.syntax.error.ArrayBoilerplateOverlaps;
import com.zarbosoft.merman.core.syntax.error.ArrayMultipleAtoms;
import com.zarbosoft.pidgoon.model.Node;
import com.zarbosoft.pidgoon.nodes.MergeSequence;
import com.zarbosoft.pidgoon.nodes.Operator;
import com.zarbosoft.pidgoon.nodes.Repeat;
import com.zarbosoft.pidgoon.nodes.Union;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROPair;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;

import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseBackArraySpec extends BackSpecData {
  public static final String NO_BOILERPLATE_YET = "";
  /** Base array element type */
  public final String type;
  public ROMap<String, BackSpec> boilerplate;
  /** Non-group atom type to back spec, for writing */
  public ROMap<String, BackSpec> splayedBoilerplate;
  protected BaseBackArraySpec(Config config) {
    super(config.id);
    this.type = config.element;
    MultiError errors = new MultiError();
    {
      TSMap<String, BackSpec> out = new TSMap<>();
      for (int i = 0; i < config.boilerplate.size(); ++i) {
        BackSpec boilerplate = config.boilerplate.get(i);
        final BackAtomSpec[] boilerplateAtom = {null};
        BackSpec.walkTypeBack(
            boilerplate,
            child -> {
              if (!(child instanceof BackAtomSpec)) return true;
              if (((BackAtomSpec) child).id != null)
                errors.add(new ArrayBoilerplateAtomIdNotNull(((BackAtomSpec) child).type));
              if (boilerplateAtom[0] != null) {
                errors.add(new ArrayMultipleAtoms(this, boilerplateAtom[0], child));
              } else {
                boilerplateAtom[0] = (BackAtomSpec) child;
              }
              return false;
            });
        if (boilerplateAtom[0] == null) {
          errors.add(new ArrayBoilerplateMissingAtom(i));
        } else {
          if (out.putReplace(boilerplateAtom[0].type, boilerplate) != null) {
            errors.add(new ArrayBoilerplateDuplicateType(boilerplateAtom[0].type));
          }
        }
      }
      this.boilerplate = out;
    }
    errors.raise();
  }

  public static SyntaxPath boilerplatePath(SyntaxPath base, String id) {
    return base.add("boilerplate").add(id);
  }

  @Override
  public ROPair<Atom, Integer> backLocate(
      Atom at, int offset, ROList<ROPair<Integer, Boolean>> segments) {
    if (segments.get(0).first != offset) return new ROPair<>(null, offset + 1);
    segments = segments.subFrom(1);
    if (segments.none()) return new ROPair<>(at, null);
    offset = 0;
    FieldArray data = (FieldArray) at.namedFields.get(id);
    for (Atom element : data.data) {
      ROPair<Atom, Integer> res = element.backLocate(offset, segments);
      if (res == null || res.first != null) return res;
      offset = res.second;
    }
    return null;
  }

  protected Node<ROList<AtomType.FieldParseResult>> buildBackRuleInnerEnd(
      Node<ROList<AtomType.AtomParseResult>> inner) {
    return new Operator<ROList<AtomType.AtomParseResult>, ROList<AtomType.FieldParseResult>>(
        inner) {
      @Override
      protected ROList<AtomType.FieldParseResult> process(ROList<AtomType.AtomParseResult> value) {
        return TSList.of(
            new AtomType.ArrayFieldParseResult(id, new FieldArray(BaseBackArraySpec.this), value));
      }
    };
  }

  protected Node<ROList<AtomType.AtomParseResult>> buildBackRuleInner(
      Environment env, Syntax syntax) {
    return new Repeat<AtomType.AtomParseResult>(
        boilerplate.none()
            ? syntax.backRuleRef(type)
            : new Union<AtomType.AtomParseResult>()
                .apply(
                    union -> {
                      TSSet<String> remaining = new TSSet<>();
                      for (AtomType core : syntax.splayedTypes.get(type)) {
                        remaining.add(core.id());
                      }
                      for (Map.Entry<String, BackSpec> plated : boilerplate) {
                        for (AtomType sub : syntax.splayedTypes.get(plated.getKey())) {
                          remaining.remove(sub.id());
                        }
                        MergeSequence<AtomType.FieldParseResult> backSeq = new MergeSequence<>();
                        backSeq.add(plated.getValue().buildBackRule(env, syntax));
                        union.add(
                            new Operator<
                                ROList<AtomType.FieldParseResult>, AtomType.AtomParseResult>(
                                backSeq) {
                              @Override
                              protected AtomType.AtomParseResult process(
                                  ROList<AtomType.FieldParseResult> value) {
                                AtomType.AtomParseResult out = null;
                                for (AtomType.FieldParseResult field : value) {
                                  if (WriteStateDeepDataArray.INDEX_KEY.equals(field.key)) continue;
                                  if (out != null) throw new Assertion();
                                  out = ((AtomType.AtomFieldParseResult) field).data;
                                }
                                return out;
                              }
                            });
                      }
                      for (String unplated : remaining) {
                        union.add(syntax.backRuleRef(unplated));
                      }
                    }));
  }

  @Override
  protected final ROList<BackSpec> walkTypeBackStep() {
    TSList<BackSpec> out = new TSList<>();
    for (Map.Entry<String, BackSpec> e : boilerplate) {
      out.add(e.getValue());
    }
    return out;
  }

  public String elementAtomType() {
    return type;
  }

  @Override
  public void finish(MultiError errors, final Syntax syntax, final SyntaxPath typePath) {
    super.finish(errors, syntax, typePath);
    TSMap<AtomType, String> overlapping = new TSMap<>();
    if (boilerplate.some()) {
      overlapping.put(syntax.gap, NO_BOILERPLATE_YET);
      overlapping.put(syntax.suffixGap, NO_BOILERPLATE_YET);
      for (AtomType base : syntax.splayedTypes.get(type)) {
        overlapping.put(base, NO_BOILERPLATE_YET); // magic value
      }
      TSMap<String, BackSpec> splayedBoilerplate = new TSMap<>();
      for (Map.Entry<String, BackSpec> boilerplateEl : boilerplate) {
        SyntaxPath boilerplatePath = boilerplatePath(typePath, boilerplateEl.getKey());
        boilerplateEl.getValue().finish(errors, syntax, boilerplatePath);
        for (AtomType leaf : syntax.splayedTypes.get(boilerplateEl.getKey())) {
          String old = overlapping.putReplace(leaf, boilerplateEl.getKey());
          if (old == null)
            errors.add(
                new ArrayBoilerplateNotInBaseSet(typePath, boilerplateEl.getKey(), leaf.id()));
          else if (!NO_BOILERPLATE_YET.equals(old))
            errors.add(new ArrayBoilerplateOverlaps(typePath, boilerplateEl.getKey(), leaf, old));
          else splayedBoilerplate.put(leaf.id(), boilerplateEl.getValue());
        }
      }
      this.splayedBoilerplate = splayedBoilerplate;
    } else {
      this.splayedBoilerplate = ROMap.empty;
    }
  }

  public SyntaxPath getPath(final FieldArray value, final int actualIndex) {
    return value.getSyntaxPath().add(Integer.toString(actualIndex));
  }

  public FieldArray get(final ROMap<String, Field> data) {
    return (FieldArray) data.getOpt(id);
  }

  /**
   * For creating synthetic wrappers when copying data - records need {}, non-luxem arrays []
   *
   * @param context
   * @param children
   */
  public abstract void copy(Context context, TSList<Atom> children);

  /**
   * For pasting out of synthetic wrappers - records have {}, non-luxem arrays []
   *
   * @param context
   * @param consumer
   */
  public abstract void uncopy(Context context, Consumer<ROList<Atom>> consumer);

  public static class Config {
    public final String id;
    public final String element;
    /**
     * Back trees with null-id BackAtom specs. The Atom types must be subsets of the base array
     * element type. Use this to remove when parsing and add when writing boilerplate required to
     * place certain atoms in this location.
     *
     * <p>Inner array is for multi-back boilerplate (for simple boilerplate use 1-element arrays)
     */
    public final ROList<BackSpec> boilerplate;

    public Config(String id, String element, ROList<BackSpec> boilerplate) {
      this.id = id;
      this.element = element;
      this.boilerplate = boilerplate;
    }
  }
}
